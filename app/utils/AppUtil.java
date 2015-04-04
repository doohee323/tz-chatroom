package utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Http.Request;

public class AppUtil {

  public static String getFormParameter(Request request, String key) {
    try {
      return request.body().asFormUrlEncoded().get(key)[0];
    } catch (Exception e) {
    }
    return null;
  }

  public static String getParameter(Request request, String key) {
    String rslt = null;
    try {
      if (request.method().equals("GET") || request.method().equals("DELETE")) {
        rslt = request.getQueryString(key);
      } else {
        if (request.body().asMultipartFormData() != null) {
          rslt = request.body().asMultipartFormData().asFormUrlEncoded().get(key)[0];
        } else if (request.body().asText() != null) {
          rslt = request.body().asText();
          JsonNode input = Json.parse(rslt);
          rslt = input.get(key).toString();
        } else if (request.body().asJson() != null) {
          rslt = request.body().asJson().get(key).getTextValue();
        } else {
          rslt = request.body().asFormUrlEncoded().get(key)[0];
        }
      }
    } catch (Exception e) {
    }
    return rslt;
  }

  public static String getString(JsonNode input, String key) {
    String str = input.get(key) == null ? "" : input.get(key).toString();
    if (str.startsWith("\"") && str.endsWith("\"")) {
      str = str.substring(1, str.length() - 1);
    }
    return str;
  }

  public static boolean checkNull(String val) {
    if (val == null || val.equals("")) {
      return true;
    }
    return false;
  }

  public static String printParams(Request request) {
    try {
      ObjectNode params = Json.newObject();
      if (request.method().equals("GET") || request.method().equals("DELETE")) {
        Map map2 = request.queryString();
        Set<String> keySet = map2.keySet();
        Iterator<String> keySetIterator = keySet.iterator();
        while (keySetIterator.hasNext()) {
          String key = keySetIterator.next();
          String val = request.getQueryString(key) == null ? "" : request.getQueryString(key);
          params.put(key, val);
        }
      } else {
        boolean bMulti = false;
        Map<String, String[]> map2 = request.body().asFormUrlEncoded();
        if (map2 == null) {
          map2 = request.body().asMultipartFormData().asFormUrlEncoded();
          bMulti = true;
        }
        Set<String> keySet = map2.keySet();
        Iterator<String> keySetIterator = keySet.iterator();
        while (keySetIterator.hasNext()) {
          String key = keySetIterator.next();
          String val = null;
          if (bMulti) {
            val = request.body().asMultipartFormData().asFormUrlEncoded().get(key)[0];
          } else {
            val = request.body().asFormUrlEncoded().get(key)[0];
          }
          params.put(key, val);
        }
      }
      params.put("request_uri", request.uri());
      params.put("request_method", request.method());
      params.put("isTest", "true");
      Logger.debug(params.toString());
      return params.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
