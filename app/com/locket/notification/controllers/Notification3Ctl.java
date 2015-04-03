package com.locket.notification.controllers;

import java.util.HashMap;
import java.util.Map;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.locket.common.message.LocketResponse;
import com.locket.common.util.AppUtil;
import com.locket.notification.models.Notification;

//@With(CorsAction.class)
public class Notification3Ctl extends Controller {

  // http://localhost:9000/api/getAllNotifications
  public static Result getAllNotifications() {
    Notification notification = new Notification();
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("notifications", notification);
    return ok(Json.toJson(rslt));
  }

  // http://localhost:9000/api/getNotification/1
  public static Result getNotification(String input) {
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("notifications", Notification.findByUserId(input));
    return ok(Json.toJson(rslt));
  }

  // CREATE
  public static Result createNotification() {
    Notification input = AppUtil.getObjFromJson(request(), new Notification());
    try {
      input.save();
    } catch (Exception e) {
      e.fillInStackTrace();
      return ok(Json.toJson(new LocketResponse("createNotification", 0, 1, e.getMessage())));
    }
    return ok(Json.toJson(input));
  }

  // CREATE form multirow
  public static Result createNotifications() {
    Map<String, Object> rslt = null;
    try {
      Notification input = AppUtil.getListFromJson(request(), new Notification());
      input.save();
      rslt = new HashMap<String, Object>();
      rslt.put("notifications", input);
    } catch (Exception e) {
      e.fillInStackTrace();
      return ok(Json.toJson(new LocketResponse("createNotifications", 0, 1, e.getMessage())));
    }
    return ok(Json.toJson(rslt));
  }

  // UPDATE
  public static Result updateNotification(String id) {
    Notification input = AppUtil.getObjFromJson(request(), new Notification());
    input.update();
    return ok(Json.toJson(input));
  }

  // DELETE
  public static Result deleteNotification(String userId) {
    Notification input = Notification.findByUserId(userId);
    input.delete();
    return ok(Json.toJson(input));
  }
}
