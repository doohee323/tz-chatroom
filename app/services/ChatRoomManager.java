package services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import models.ChatRoom;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Akka;
import play.libs.Json;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import scala.concurrent.duration.Duration;
import utils.AppUtil;
import akka.actor.Cancellable;

public class ChatRoomManager {
  private static org.slf4j.Logger Logger = org.slf4j.LoggerFactory.getLogger(ChatRoomManager.class);
  public static Map<String, ObjectNode> chatrooms = new HashMap<String, ObjectNode>();
  private static Map<String, Cancellable> chatroomSchedule = new HashMap<String, Cancellable>();

  public static Map<String, ObjectNode> getChatRooms() {
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

          Cancellable scheduler = ChatRoomManager.getScheduler(room.get("name").asText());
          chatroomSchedule.put(key2, scheduler);
        }
      } catch (Exception e) {
        Logger.error(e.getMessage());
        e.printStackTrace();
        return new HashMap<String, ObjectNode>();
      } finally {
        RedisManager.getInstance().returnJedis(jedis);
      }
    }
    return chatrooms;
  }

  public static boolean insertChatRoom(String chatroom) {
    Jedis jedis = RedisManager.getInstance().getJedis();
    try {
      if (!AppUtil.isEnoughMemory()) {
        return false;
      }

      String key = ChatRoom.ROOT + ChatRoom.CHATROOM + chatroom;
      jedis.set(key, chatroom);

      ObjectNode room = Json.newObject();
      room.put("name", chatroom);
      chatrooms.put(key, room);

      Cancellable scheduler = ChatRoomManager.getScheduler(room.get("name").asText());
      chatroomSchedule.put(key, scheduler);
    } catch (Exception e) {
      Logger.error(e.getMessage());
      e.printStackTrace();
      return false;
    } finally {
      RedisManager.getInstance().returnJedis(jedis);
    }
    return true;
  }

  public static boolean deleteChatRoom(String chatroom) {
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
      Logger.error(e.getMessage());
      e.printStackTrace();
      return false;
    } finally {
      RedisManager.getInstance().returnJedis(jedis);
    }
    return true;
  }

  public static class RunnableListener implements Runnable {
    private String chatroom;

    public RunnableListener(String chatroom) {
      this.chatroom = chatroom;
    }

    public void run() {
      String jchannel = ChatRoom.ROOT + chatroom + ":" + ChatRoom.CHANNEL;
      Logger.debug("!!!!!!!! listening to " + jchannel);
      Jedis j = RedisManager.getInstance().getJedis();
      j.subscribe(new RedisListener(), jchannel);
    }
  }

  public static Cancellable getScheduler(String chatroom) {
    Cancellable scheduler =
        Akka.system()
            .scheduler()
            .scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS),
                new RunnableListener(chatroom) {
                }, Akka.system().dispatcher());
    return scheduler;
  }

  /**
   */
  public static WebSocket<String> connect(final String params) {
    return new WebSocket<String>() {
      public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
        try {
          new ChatRoom().join(params, null, in, out);
        } catch (Exception e) {
          Logger.error(e.getMessage());
          e.printStackTrace();
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