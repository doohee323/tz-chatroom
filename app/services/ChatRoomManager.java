package services;

import java.util.concurrent.TimeUnit;

import models.ChatRoom;
import play.libs.Akka;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import scala.concurrent.duration.Duration;
import akka.actor.Cancellable;

public class ChatRoomManager {

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