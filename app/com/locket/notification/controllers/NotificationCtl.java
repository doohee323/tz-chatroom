package com.locket.notification.controllers;

import java.util.HashMap;
import java.util.Map;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

import com.google.gson.Gson;
import com.locket.common.annotation.CorsAction;
import com.locket.notification.daos.EmailNodificationDAO;
import com.locket.notification.daos.NotificationDAO;
import com.locket.notification.models.Notification;

@With(CorsAction.class)
public class NotificationCtl extends Controller {

  static NotificationDAO dao = new NotificationDAO();

  // http://localhost:9000/api/sendEmailFromEvent/20110101
  public static Result sendEmailFromEvent(String requestAt) {
    EmailNodificationDAO service = new EmailNodificationDAO();
    service.sendEmailFromEvent(requestAt);
    return ok(Json.toJson(service.sendEmailFromEvent(requestAt)));
  }

  // http://localhost:9000/api/getAllNotifications
  public static Result getAllNotifications() {
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("notifications", dao.getAllNotifications());
    return ok(Json.toJson(rslt));
  }

  // http://localhost:9000/api/getNotification/20140208
  public static Result getNotification(String input) {
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("notifications", dao.getNotification(input));
    return ok(Json.toJson(rslt));
  }

  // CREATE
  public static Result createNotification() {
    String json = request().body().asText();
    json = "{userId:'1', title:'title1'}";
    Notification input = new Gson().fromJson(json, Notification.class);
    return ok(Json.toJson(dao.createNotification(input)));
  }

  // UPDATE
  public static Result updateNotification() {
    String json = request().body().asText();
    Notification input = new Gson().fromJson(json, Notification.class);
    return ok(Json.toJson(dao.updateNotification(input)));
  }

  // DELETE
  public static Result deleteNotification() {
    String json = request().body().asText();
    Notification input = new Gson().fromJson(json, Notification.class);
    return ok(Json.toJson(dao.deleteNotification(input)));
  }
}
