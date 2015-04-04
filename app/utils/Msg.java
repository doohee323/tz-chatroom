package utils;

import models.Response;

import org.codehaus.jackson.JsonNode;

import play.libs.Json;

public enum Msg {
  SUCCESS(0, "success", null), FAIL(-1, "fail", null), DATABASE(100,
      "A database connection error has occured.", null), DUPLICATE(-2, "This data already exists.",
      null), NOSESSION(-9, "No session", null);

  private static org.slf4j.Logger Logger = org.slf4j.LoggerFactory.getLogger(Msg.class);

  private int code;
  private String msg;
  private String rslt;

  private Msg(int code, String msg, String rslt) {
    this.code = code;
    this.msg = msg;
    this.rslt = rslt;
  }

  public String getMsg() {
    return msg;
  }

  public int getCode() {
    return code;
  }

  public int setCode(int code) {
    return this.code = code;
  }

  public String getRslt() {
    return rslt;
  }

  @Override
  public String toString() {
    return code + ": " + msg;
  }

  public JsonNode toJson() {
    if (this.getCode() != 0) {
      Logger.error(this.getMsg());
    }
    return Json.toJson(new Response(this.getCode(), this.getMsg()));
  }

  public JsonNode toJson(Object obj) {
    return toJson(obj, null);
  }

  public JsonNode toJson(Object obj, String rslt) {
    String msg = "";
    int code = this.getCode();
    if (this.getCode() != 0) {
      if (obj instanceof Exception) {
        msg = ((Exception) obj).getMessage();
      } else if (obj instanceof Integer) {
        code = (Integer) obj;
      } else {
        msg = (String) obj;
      }
      Logger.error(msg);
    }
    if (msg == null || msg.equals(""))
      msg = this.getMsg();
    if (msg.indexOf("error[Duplicate entry") > -1) {
      code = -2;
    }
    if (code != 0) {
      Logger.error(msg);
    }
    String rsltStr = Json.stringify(Json.toJson(new Response(code, msg)));
    if (rslt != null) {
      try {
        rsltStr = rsltStr.substring(0, rsltStr.length() - 1);
        rslt = rslt.trim();
        if (rslt.startsWith("{"))
          rslt = rslt.substring(1, rslt.length() - 1);
        if (rslt.indexOf(":") == -1) {
          rslt = "\"detail\":\"" + rslt + "\"";
        }
        rsltStr += "," + rslt + "}";
      } catch (Exception e) {
        Logger.error(e.getMessage());
      }
    }
    Logger.debug(rsltStr);
    return Json.parse(rsltStr);
  }
}
