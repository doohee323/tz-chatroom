package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.ChatRoom;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import services.ChatRoomManager;
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

      Cancellable scheduler = ChatRoomManager.getScheduler(room.get("name").asText());
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

          Cancellable scheduler = ChatRoomManager.getScheduler(room.get("name").asText());
          chatroomSchedule.put(key2, scheduler);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return ok(Msg.FAIL.toJson());
      } finally {
        RedisManager.getInstance().returnJedis(jedis);
      }
    }

    String returnStr = "{\"result\":" + AppUtil.map2array(chatrooms).toString() + "}";
    return ok(Msg.SUCCESS.toJson(0, returnStr));
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
        return ChatRoomManager.connect(params2);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    } else {
      return ChatRoomManager.connect(params);
    }
  }

}