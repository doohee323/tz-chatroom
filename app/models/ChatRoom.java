package models;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import scala.concurrent.duration.Duration;
import services.RedisManager;

/**
 */
public class ChatRoom { 

  public static final String CHATROOM = "topzone:chatroom:";
  public static final String MEMBERS = "members:";
  public static final String CHANNEL = "messages:";
  private static Map<String, WebSocket.Out<String>> members;

  public ChatRoom() {
    members = new HashMap<String, WebSocket.Out<String>>();
    
//    Jedis j = RedisManager.getInstance().getJedis();
//    Set<String> members = j.smembers(MEMBERS);
    
    Akka.system().scheduler()
        .scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS), new Runnable() {
          public void run() {
            String jchannel = CHATROOM + CHANNEL;
            Jedis j = RedisManager.getInstance().getJedis();
            j.subscribe(new MyListener(), jchannel);
          }
        }, Akka.system().dispatcher());
  }

  /**
   */
  public void join(final String username, WebSocket.In<String> in, WebSocket.Out<String> out)
      throws Exception {

    onReceive(new Join(username, out));

    in.onMessage(new Callback<String>() {
      public void invoke(String event) {
        if(event == null || event.equals("")) return;
        Jedis j = RedisManager.getInstance().getJedis();
        try {
          String jchannel = CHATROOM + CHANNEL;
          JsonNode json = Json.parse(event);
          Talk talk = new Talk(username, json.get("name").toString());
          j.publish(jchannel, Json.stringify(Json.toJson(talk)));
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          RedisManager.getInstance().returnJedis(j);
        }
      }
    });

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

  public static void remoteMessage(String username, String message) {
    WebSocket.Out<String> channel = members.get(username);
    channel.write(message);
  }

  public void onReceive(Object message) throws Exception {
    Jedis j = RedisManager.getInstance().getJedis();
    try {
      String jmembers = CHATROOM + MEMBERS;
      if (message instanceof Join) {
        Join join = (Join) message;
        if (j.sismember(jmembers, join.username)) {
          ChatRoom.remoteMessage(join.username, "This username is already used!");
        } else {
          members.put(join.username, join.channel);
          j.sadd(jmembers, join.username);
          ChatRoom.remoteMessage(join.username, "OK");
        }
      } else if (message instanceof Quit) {
        Quit quit = (Quit) message;
        members.remove(quit.username);
        j.srem(jmembers, quit.username);
      } else if (message instanceof Talk) {
        Talk talk = (Talk) message;
        notifyAll("talk", talk.username, talk.text);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      RedisManager.getInstance().returnJedis(j);
    }
  }

  public void notifyAll(String kind, String user, String text) {
    Jedis j = RedisManager.getInstance().getJedis();
    try {
      String jmembers = CHATROOM + MEMBERS;
      for (WebSocket.Out<String> channel : members.values()) {
        ObjectNode event = Json.newObject();
        event.put("kind", kind);
        event.put("user", user);
        event.put("message", text);
        ArrayNode m = event.putArray("members");
        for (String u : j.smembers(jmembers)) {
          m.add(u);
        }
        channel.write(event.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      RedisManager.getInstance().returnJedis(j);
    }
  }

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
      JsonNode json = Json.parse(messageBody);
      ChatRoom.remoteMessage(json.get("username").asText(), messageBody);
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
