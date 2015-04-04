package controllers;

import models.ChatRoom;

import org.codehaus.jackson.JsonNode;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

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

  /**
   * Display the chat room.
   */
  public static Result chatRoom(String username) {
    if (username == null || username.trim().equals("")) {
      flash("error", "Please choose a valid username.");
      // return redirect(routes.Application.index());
    }

    return ok();
  }

  public static Result chatRoomJs(String username) {
    return ok();
  }

  /**
   * Handle the chat websocket.
   */
  public static WebSocket<String> chat(final String username) {
    return new WebSocket<String>() {

      // Called when the Websocket Handshake is done.
      public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {

        // Join the chat room.
        try {
          new ChatRoom().join(username, in, out);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    };
  }
}