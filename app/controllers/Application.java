package controllers;

import java.util.Map;

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

public class Application extends Controller {

  private static org.slf4j.Logger Logger = org.slf4j.LoggerFactory.getLogger(Application.class);

  /*
   * GET /chatroom/join/:username controllers.Application.join(username)
   */
  public static Result join(String username) {
    session().put("username", username);
    return ok(Msg.SUCCESS.toJson());
  }

  /*
   * POST /chatroom controllers.Application.insertChatRoom()
   */
  public static Result insertChatRoom() {
    String chatroom = AppUtil.getParameter(request(), "chatroom");
    if (ChatRoomManager.insertChatRoom(chatroom)) {
      return ok(Msg.SUCCESS.toJson());
    } else {
      return ok(Msg.FAIL.toJson());
    }
  }

  /*
   * DELETE /chatroom controllers.Application.deleteChatRoom()
   */
  public static Result deleteChatRoom() {
    String chatroom = request().getQueryString("name");
    if (ChatRoomManager.deleteChatRoom(chatroom)) {
      return ok(Msg.SUCCESS.toJson());
    } else {
      return ok(Msg.FAIL.toJson());
    }
  }

  /*
   * GET /chatrooms controllers.Application.chatRooms()
   */
  public static Result chatRooms() {
    Map<String, ObjectNode> chatrooms = ChatRoomManager.getChatRooms();
    if (!chatrooms.isEmpty()) {
      return ok(Msg.SUCCESS.toJson(0, AppUtil.map2array(chatrooms)));
    } else {
      return ok(Msg.FAIL.toJson());
    }
  }

  /*
   * GET /chatroom/:chatroom controllers.Application.chatRoom(chatroom)
   */
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
   * GET /chatroom/chat/:param controllers.Application.chat(param)
   */
  public static WebSocket<String> chat(final String params) {
    Logger.debug("Username from request = " + request().remoteAddress());
    JsonNode json = Json.parse(params);
    if (json.has("type")
        && (json.get("type").asText().equals("join") || json.get("type").asText().equals("rejoin"))) {
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

  /**
   * for binary websocket frames
   */
  public static WebSocket<byte[]> chat2(final String params) {
    return null;
  }

}