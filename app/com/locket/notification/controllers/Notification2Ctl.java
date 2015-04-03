package com.locket.notification.controllers;

import java.util.HashMap;
import java.util.Map;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.locket.notification.daos.Notification2DAO;

public class Notification2Ctl extends Controller {

  static Notification2DAO dao = new Notification2DAO();

  // GET : http://localhost:9000/api/getAllNotifications
  public static Result getAllNotifications() {
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("notifications", dao.getAllNotifications());
    return ok(Json.toJson(rslt));
  }

  // GET : http://localhost:9000/api/getNotification/20140208
  public static Result getNotification(String requestAt) {
    Map<String, String> input = new HashMap<String, String>();
    input.put("requestAt", requestAt);
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("notifications", dao.getAllNotifications());
    return ok(Json.toJson(rslt));
  }

  // CREATE
  public static Result createNotification(String requestAt) {
    Map<String, String> input = new HashMap<String, String>();
    input.put("requestAt", requestAt);
    return ok(Json.toJson(dao.createNotification(input)));
  }

  // UPDATE
  public static Result updateNotification(String requestAt) {
    Map<String, String> input = new HashMap<String, String>();
    input.put("requestAt", requestAt);
    return ok(Json.toJson(dao.getNotification(input)));
  }

  // DELETE
  public static Result delleteNotification(String requestAt) {
    Map<String, String> input = new HashMap<String, String>();
    input.put("requestAt", requestAt);
    return ok(Json.toJson(dao.getNotification(input)));
  }
}
