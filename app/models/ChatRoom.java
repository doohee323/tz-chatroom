package models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import services.ChatRoomManager;
import services.RedisManager;
import utils.AppUtil;

/**
 */
public class ChatRoom {

  private static org.slf4j.Logger Logger = org.slf4j.LoggerFactory.getLogger(ChatRoom.class);
  
  public static final String ROOT = "topzone:";
  public static final String CHATROOM = "chatroom:";
  public static final String CHANNEL = "messages:";
  public static Map<String, WebSocket.Out<String>> members =
      new HashMap<String, WebSocket.Out<String>>();

  /**
   * join to websocket session
   */
  public void join(final String params, final String username, WebSocket.In<String> in,
      WebSocket.Out<String> out) throws Exception {
    JsonNode json = Json.parse(params);
    onReceive(json, out);

    in.onMessage(new Callback<String>() {
      public void invoke(String event) {
        if (event == null || event.equals(""))
          return;

        Jedis j = RedisManager.getInstance().getJedis();
        try {
          if (ChatRoomManager.chatrooms.isEmpty()) {
            ChatRoomManager.getChatRooms();
          }
          JsonNode json = Json.parse(event);
          String jchannel = ROOT + json.get("chatroom").asText() + ":" + CHANNEL;
          Logger.debug("joined to " + jchannel);
          j.publish(jchannel, json.toString());
        } catch (Exception e) {
          Logger.error(e.getMessage());
          e.printStackTrace();
        } finally {
          RedisManager.getInstance().returnJedis(j);
        }
      }
    });

    in.onClose(new Callback0() {
      public void invoke() {
        try {
          JsonNode json = Json.parse(params);
          ObjectNode event = Json.newObject();
          event.put("type", "quit");
          event.put("username", json.get("username").asText());
          event.put("chatroom", json.get("chatroom").asText());
          onReceive(Json.parse(event.toString()), null);
        } catch (Exception e) {
          Logger.error(e.getMessage());
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * write to a channel
   */
  public static void remoteMessage(WebSocket.Out<String> channel, String message) {
    channel.write(message);
  }

  /**
   * broadcast to all channels in a chatroom
   */
  public static void broadcast(String chatroom, String message) {
    Logger.debug("broadcast to " + chatroom + ": " + message);
    Set<String> set = members.keySet();
    Iterator<String> iter = set.iterator();
    while (iter.hasNext()) {
      String key = iter.next().toString();
      if (key.indexOf(chatroom) > -1) {
        WebSocket.Out<String> channel = (WebSocket.Out<String>) members.get(key);
        if (channel != null) {
          channel.write(message);
        }
      }
    }
  }

  /**
   * handling according to the payload
   */
  public void onReceive(JsonNode json, WebSocket.Out<String> channel) throws Exception {
    Jedis j = RedisManager.getInstance().getJedis();
    try {
      String type = json.has("type") ? json.get("type").asText() : null;
      String chatroom = json.has("chatroom") ? json.get("chatroom").asText() : null;
      String username = json.has("username") ? json.get("username").asText() : null;
      String text = json.has("text") ? json.get("text").asText() : null;

      if (type.equals("join") || type.equals("rejoin")) {
        if (AppUtil.isEnoughMemory()) {
          if (username != null) {
            String jmembers = ROOT + chatroom + ":" + username;
            if (j.sismember(jmembers, username)) {
              ChatRoom.remoteMessage(channel, "This username is already used!");
              Logger.debug("This username is already used!" + jmembers);
            } else {
              members.put(jmembers, channel);
              j.sadd(jmembers, username);
              String ipaddr = json.has("ipaddr") ? json.get("ipaddr").asText() : "";
              String msg = ipaddr + " joined the party!(" + username + ")";
              Logger.debug("join: " + msg);
              ChatRoom.broadcast(ROOT + chatroom, msg);
            }
          }
        }
      } else if (type.equals("quit")) {
        String jmembers = ROOT + chatroom + ":" + username;
        members.remove(jmembers);
        j.srem(jmembers, username);
        String ipaddr = json.has("ipaddr") ? json.get("ipaddr").asText() : "";
        String msg = ipaddr + " passed out...(" + username + ")";
        Logger.debug("quit: " + msg);
        ChatRoom.broadcast(ROOT + chatroom, msg);
      } else if (type.equals("talk")) {
        ChatRoom.broadcast(ROOT + chatroom, text);
      }
    } catch (Exception e) {
      Logger.error(e.getMessage());
      e.printStackTrace();
    } finally {
      RedisManager.getInstance().returnJedis(j);
    }
  }
}
