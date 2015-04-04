package models;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import services.RedisManager;

/**
 */
public class ChatRoom {

  private static final String CHANNEL = "messages";
  private static final String MEMBERS = "members";

  /**
   * Join the default room.
   */
  public void join(final String username, WebSocket.In<String> in, WebSocket.Out<String> out)
      throws Exception {
    System.out.println("joining: " + username);
    onReceive(new Join(username, out));

    // For each event received on the socket,
    in.onMessage(new Callback<String>() {
      public void invoke(String event) {
        Jedis j = RedisManager.getInstance().getJedis();
        try {
          System.out.println(username);
          System.out.println(event);
          JsonNode json = Json.parse(event);
          Talk talk = new Talk(username, json.get("name").toString());
          // All messages are pushed through the pub/sub channel
          j.publish(ChatRoom.CHANNEL, Json.stringify(Json.toJson(talk)));
        } catch (Exception e) {
          System.out.println(e.getMessage());
        } finally {
          RedisManager.getInstance().returnJedis(j);
        }
      }
    });

    // When the socket is closed.
    in.onClose(new Callback0() {
      public void invoke() {
        System.out.println("");
        try {
          onReceive(new Quit(username));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public static void remoteMessage(Object message) {
  }

  Map<String, WebSocket.Out<String>> members = new HashMap<String, WebSocket.Out<String>>();

  public void onReceive(Object message) throws Exception {
    Jedis j = RedisManager.getInstance().getJedis();

    try {
      if (message instanceof Join) {
        Join join = (Join) message;
        if (j.sismember(MEMBERS, join.username)) {
          // getSender().tell("This username is already used", getSelf());
        } else {
          members.put(join.username, join.channel);
          j.sadd(MEMBERS, join.username);
          // getSender().tell("OK", getSelf());
        }
      } else if (message instanceof Quit) {
        Quit quit = (Quit) message;
        members.remove(quit.username);
        j.srem(MEMBERS, quit.username);
      } else if (message instanceof Talk) {
        Talk talk = (Talk) message;
        notifyAll("talk", talk.username, talk.text);
      }
    } finally {
      RedisManager.getInstance().returnJedis(j);
    }
  }

  public void notifyAll(String kind, String user, String text) {
    for (WebSocket.Out<String> channel : members.values()) {

      ObjectNode event = Json.newObject();
      event.put("kind", kind);
      event.put("user", user);
      event.put("message", text);

      ArrayNode m = event.putArray("members");

      // Go to Redis to read the full roster of members. Push it down with the
      // message.
      Jedis j = RedisManager.getInstance().getJedis();
      try {
        for (String u : j.smembers(MEMBERS)) {
          m.add(u);
        }
      } finally {
        RedisManager.getInstance().returnJedis(j);
      }

      channel.write(event.toString());
    }
  }

  // -- Messages
  public class Join {
    final String username;
    final WebSocket.Out<String> channel;

    public String getUsername() {
      return username;
    }

    public String getType() {
      return "join";
    }

    public Join(String username, WebSocket.Out<String> channel) {
      this.username = username;
      this.channel = channel;
    }
  }

  public class Talk {
    final String username;
    final String text;

    public String getUsername() {
      return username;
    }

    public String getText() {
      return text;
    }

    public String getType() {
      return "talk";
    }

    public Talk(String username, String text) {
      this.username = username;
      this.text = text;
    }
  }

  public class Quit {
    final String username;

    public String getUsername() {
      return username;
    }

    public String getType() {
      return "quit";
    }

    public Quit(String username) {
      this.username = username;
    }
  }

  public class MyListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String messageBody) {
      // Process messages from the pub/sub channel
      JsonNode json = Json.parse(messageBody);
      Object message = null;
      String messageType = json.get("type").asText();
      if ("talk".equals(messageType)) {
        message = new Talk(json.get("username").asText(), json.get("text").asText());
      } else if ("quit".equals(messageType)) {
        message = new Quit(json.get("username").asText());
      }
      ChatRoom.remoteMessage(message);
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
