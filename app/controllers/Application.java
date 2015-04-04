package controllers;

import java.util.Set;

import models.ChatRoom;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import services.RedisManager;
import utils.AppUtil;
import utils.Msg;

public class Application extends Controller {
  // public static WebSocket<String> pingWs() {
  // return new WebSocket<String>() {
  // public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
  // System.out.println();
  //
  // out.write("Hello!");
  // // out.close();
  //
  // in.onClose(new Callback0() {
  // @Override
  // public void invoke() throws Throwable {
  // System.out.println();
  // }
  // });
  // }
  // };
  // }

  /**
   * Display the home page.
   */
  public static Result index() {
    return ok();
  }

  public static Result insertChatRoom() {
    String chatroom = AppUtil.getParameter(request(), "chatroom");
    Jedis jedis = RedisManager.getInstance().getJedis();
    try {
      String key = ChatRoom.CHATROOM + chatroom;
      jedis.set(key, chatroom);
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
      String key = ChatRoom.CHATROOM + chatroom;
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
    Jedis jedis = RedisManager.getInstance().getJedis();
    try {
      JsonFactory factory = new JsonFactory();
      ObjectMapper om = new ObjectMapper(factory);
      factory.setCodec(om);
      ArrayNode arry = om.createArrayNode();
      String key = ChatRoom.CHATROOM + "*";
      Set<String> set = jedis.keys(key);
      for (String i : set) {
        String val = jedis.get(i);
        ObjectNode room = Json.newObject();
        room.put("name", val);
        arry.add(room);
      }
      String returnStr = "{\"result\":" + arry.toString() + "}";
      return ok(Msg.SUCCESS.toJson(0, returnStr));
    } catch (Exception e) {
      e.printStackTrace();
      return ok(Msg.FAIL.toJson());
    } finally {
      RedisManager.getInstance().returnJedis(jedis);
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
  public static WebSocket<String> chat(final String username) {
    return new WebSocket<String>() {
      public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
        try {
          new ChatRoom().join(username, in, out);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    };
  }
}