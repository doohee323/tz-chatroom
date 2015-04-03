package com.locket.pattern.controllers;

import java.util.HashMap;
import java.util.Map;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.locket.common.message.LocketResponse;
import com.locket.common.util.AppUtil;
import com.locket.pattern.models.Region;

/**
 * @author TZ
 * 
 */

public class RegionRestCtl extends Controller {

  public static Result getRegion(String queryCenterCode, String input) {
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("uip_regions", Region.findByCenterCode(queryCenterCode, input));
    return ok(Json.toJson(rslt));
  }

  public static Result getRegion2(String input) {
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("uip_regions", Region.findByCode(input));
    return ok(Json.toJson(rslt));
  }

  public static Result getAllRegions() {
    Region region = new Region();
    Map<String, Object> rslt = new HashMap<String, Object>();
    rslt.put("uip_regions", region);
    return ok(Json.toJson(rslt));
  }

  // CREATE
  public static Result createRegion() {
    Region input = AppUtil.getObjFromJson(request(), new Region());
    try {
      input.save();
    } catch (Exception e) {
      e.fillInStackTrace();
      return ok(Json.toJson(new LocketResponse("createRegion", 0, 1, e.getMessage())));
    }
    return ok(Json.toJson(input));
  }

  // CREATE form multirow
  public static Result createRegions() {
    Map<String, Object> rslt = null;
    try {
      Region input = AppUtil.getListFromJson(request(), new Region());
      input.save();
      rslt = new HashMap<String, Object>();
      rslt.put("uip_regions", input);
    } catch (Exception e) {
      e.fillInStackTrace();
      return ok(Json.toJson(new LocketResponse("createRegions", 0, 1, e.getMessage())));
    }
    return ok(Json.toJson(rslt));
  }

  // UPDATE
  public static Result updateRegion(String id) {
    Region input = AppUtil.getObjFromJson(request(), new Region());
    input.update();
    return ok(Json.toJson(input));
  }

  // DELETE
  public static Result deleteRegion(String code) {
    Region input = Region.findByCode(code);
    input.delete();
    return ok(Json.toJson(input));
  }
}

