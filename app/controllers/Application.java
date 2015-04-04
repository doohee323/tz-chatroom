package controllers;

import models.ChatRoom;
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

  public static Result saveChatRooms() {
    String chatroom = AppUtil.getParameter(request(), "chatroom");
    Jedis jedis = RedisManager.getInstance().getJedis();
    try {
      String key = ChatRoom.CHATROOM + "*";
      jedis.set(key, chatroom);
    } catch (Exception e) {
      e.printStackTrace();
      return ok(Msg.FAIL.toJson());
    } finally {
      RedisManager.getInstance().returnJedis(jedis);
    }
    return ok(Msg.SUCCESS.toJson());
  }

  public static Result chatRooms() {
    Jedis jedis = RedisManager.getInstance().getJedis();
    String rsltStr = null;
    try {
      String key = ChatRoom.CHATROOM + "*";
      rsltStr = jedis.get(key) == null ? "" : jedis.get(key);
    } catch (Exception e) {
      e.printStackTrace();
      return ok(Msg.FAIL.toJson());
    } finally {
      RedisManager.getInstance().returnJedis(jedis);
    }
    return ok(Json.parse(rsltStr));
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