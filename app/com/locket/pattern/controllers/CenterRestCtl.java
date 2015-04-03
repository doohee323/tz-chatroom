package com.locket.pattern.controllers;

import java.util.HashMap;
import java.util.Map;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.locket.common.message.LocketResponse;
import com.locket.common.util.AppUtil;
import com.locket.pattern.models.Center;

/**
 * @author TZ
 * 
 */

public class CenterRestCtl extends Controller {

  public static Result getCenter(String input) {
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("uip_centers", Center.findByCode(input));
    return ok(Json.toJson(rslt));
  }

  public static Result getAllCenters() {
    Center center = new Center();
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("uip_centers", center);
    return ok(Json.toJson(rslt));
  }

  // CREATE
  public static Result createCenter() {
    Center input = AppUtil.getObjFromJson(request(), new Center());
    try {
      input.save();
    } catch (Exception e) {
      e.fillInStackTrace();
      return ok(Json.toJson(new LocketResponse("createCenter", 0, 1, e.getMessage())));
    }
    return ok(Json.toJson(input));
  }

  // CREATE form multirow
  public static Result createCenters() {
    Map<String, Object> rslt = null;
    try {
      Center input = AppUtil.getListFromJson(request(), new Center());
      input.save();
      rslt = new HashMap<String, Object>();
      rslt.put("uip_centers", input);
    } catch (Exception e) {
      e.fillInStackTrace();
      return ok(Json.toJson(new LocketResponse("createCenters", 0, 1, e.getMessage())));
    }
    return ok(Json.toJson(rslt));
  }

  // UPDATE
  public static Result updateCenter(String id) {
    Center input = AppUtil.getObjFromJson(request(), new Center());
    input.update();
    return ok(Json.toJson(input));
  }

  // DELETE
  public static Result deleteCenter(String code) {
    Center input = Center.findByCode(code);
    input.delete();
    return ok(Json.toJson(input));
  }
}
