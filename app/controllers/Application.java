package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import models.ChatRoom;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import scala.concurrent.duration.Duration;
import services.RedisManager;
import utils.AppUtil;
import utils.Msg;
import akka.actor.Cancellable;

public class Application extends Controller {

  private static Map<String, ObjectNode> chatrooms = new HashMap<String, ObjectNode>();
  private static Map<String, Cancellable> chatroomSchedule = new HashMap<String, Cancellable>();

  /**
   */
  public static Result index() {
    return ok();
  }

  public static Result join(String username) {
    session().put("username", username);
    return ok(Msg.SUCCESS.toJson());
  }

  public static Result insertChatRoom() {
    String chatroom = AppUtil.getParameter(request(), "chatroom");
    Jedis jedis = RedisManager.getInstance().getJedis();
    try {
      String key = ChatRoom.ROOT + ChatRoom.CHATROOM + chatroom;
      jedis.set(key, chatroom);

      ObjectNode room = Json.newObject();
      room.put("name", chatroom);
      chatrooms.put(key, room);

      Cancellable scheduler =
          Akka.system()
              .scheduler()
              .scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS),
                  new RunnableListener(room.get("name").asText()) {
                  }, Akka.system().dispatcher());
      chatroomSchedule.put(key, scheduler);
    } catch (Exception e) {
      e.printStackTrace();
      return ok(Msg.FAIL.toJson());
    } finally {
      RedisManager.getInstance().returnJedis(jedis);
    }
    return ok(Msg.SUCCESS.toJson());
  }

  public static Result deleteChatRoom() {
    String chatroom = request().getQueryString("name");
    Jedis jedis = RedisManager.getInstance().getJedis();
    try {
      String key = ChatRoom.ROOT + ChatRoom.CHATROOM + chatroom;
      Cancellable scheduler = chatroomSchedule.get(key);
      scheduler.cancel();
      chatroomSchedule.remove(key);
      chatrooms.remove(key);
      String key2 = ChatRoom.ROOT + chatroom + ":*";
      Set<String> set = jedis.keys(key2);
      for (String user : set) {
        String username = user.substring(user.lastIndexOf(":") + 1, user.length());
        String jmembers = ChatRoom.ROOT + chatroom + ":" + username;
        ChatRoom.members.remove(jmembers);
        String msg = chatroom + " is closed!";
        ChatRoom.broadcast(ChatRoom.ROOT + chatroom, msg);
        jedis.del(user);
      }
      jedis.del(key);
    } catch (Exception e) {
      e.printStackTrace();
      return ok(Msg.FAIL.toJson());
    } finally {
      RedisManager.getInstance().returnJedis(jedis);
    }
    return ok(Msg.SUCCESS.toJson());
  }

  public static Result chatRooms(String page) {
    if (chatrooms.isEmpty()) {
      Jedis jedis = RedisManager.getInstance().getJedis();
      try {
        String key = ChatRoom.ROOT + ChatRoom.CHATROOM + "*";
        Set<String> set = jedis.keys(key);
        for (String i : set) {
          String val = jedis.get(i);
          ObjectNode room = Json.newObject();
          room.put("name", val);
          String key2 = ChatRoom.ROOT + ChatRoom.CHATROOM + val;
          chatrooms.put(key2, room);

          Cancellable scheduler =
              Akka.system()
                  .scheduler()
                  .scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS),
                      new RunnableListener(room.get("name").asText()) {
                      }, Akka.system().dispatcher());
          chatroomSchedule.put(key2, scheduler);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return ok(Msg.FAIL.toJson());
      } finally {
        RedisManager.getInstance().returnJedis(jedis);
      }
    }

    JsonFactory factory = new JsonFactory();
    ObjectMapper om = new ObjectMapper(factory);
    factory.setCodec(om);
    ArrayNode chatroomArry = om.createArrayNode();

    Set<String> set = chatrooms.keySet();
    Iterator<String> iter = set.iterator();
    while (iter.hasNext()) {
      String key = iter.next().toString();
      chatroomArry.add((ObjectNode) chatrooms.get(key));
    }
    String returnStr = "{\"result\":" + chatroomArry.toString() + "}";
    return ok(Msg.SUCCESS.toJson(0, returnStr));
  }

  public static class RunnableListener implements Runnable {
    private String chatroom;

    public RunnableListener(String chatroom) {
      this.chatroom = chatroom;
    }

    public void run() {
      String jchannel = ChatRoom.ROOT + chatroom + ":" + ChatRoom.CHANNEL;
      System.out.println("!!!!!!!! listening to " + jchannel);
      Jedis j = RedisManager.getInstance().getJedis();
      j.subscribe(new RedisListener(), jchannel);
    }
  }

  public static Result chatRoom(String chatroom) {
    if (chatroom == null || chatroom.trim().equals("")) {
      return ok(Msg.FAIL.toJson(), "{\"Please choose a valid username.\"}");
    }
    Jedis jedis = RedisManager.getInstance().getJedis();
    String rsltStr = null;
    try {
      String key = ChatRoom.CHATROOM + chatroom;
      rsltStr = jedis.get(key) == null ? "" : jedis.get(key);
    } catch (Exception e) {
      e.printStackTrace();
      return ok(Msg.FAIL.toJson());
    } finally {
      RedisManager.getInstance().returnJedis(jedis);
    }
    return ok(Json.parse(rsltStr));
  }

  /**
   * join
   */
  public static WebSocket<String> chat(final String params) {
    System.out.println("Username from request = " + request().remoteAddress());
    JsonNode json = Json.parse(params);
    if (json.has("type") && json.get("type").asText().equals("join")) {
      try {
        ObjectNode event = Json.newObject();
        event.put("type", json.get("type").asText());
        event.put("username", json.get("username").asText());
        event.put("chatroom", json.get("chatroom").asText());
        event.put("ipaddr", request().remoteAddress());
        String params2 = event.toString();
        return connect(params2);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    } else {
      return connect(params);
    }
  }

  /**
   */
  public static WebSocket<String> connect(final String params) {
    return new WebSocket<String>() {
      public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
        try {
          new ChatRoom().join(params, null, in, out);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    };
  }

  public static class RedisListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String messageBody) {
      String channel2 = channel.replaceAll(ChatRoom.CHANNEL, "");
      channel2 = channel2.substring(0, channel2.length() - 1);
      ChatRoom.broadcast(channel2, messageBody);
    }

    @Override
    public void onPMessage(String arg0, String arg1, String arg2) {
    }

    @Override
    public void onPSubscribe(String arg0, int arg1) {
    }

    @Override
    public void onPUnsubscribe(String arg0, int arg1) {
    }

    @Override
    public void onSubscribe(String arg0, int arg1) {
    }

    @Override
    public void onUnsubscribe(String arg0, int arg1) {
    }
  }
}