package utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import controllers.Application;
import play.Play;
import play.libs.Json;
import play.mvc.Http.Request;

public class AppUtil {

  private static org.slf4j.Logger Logger = org.slf4j.LoggerFactory.getLogger(Application.class);

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
      e.printStackTrace();
    }
    return rslt;
  }

  public static String map2array(Map<String, ObjectNode> input) {
    ArrayNode array = null;
    try {
      JsonFactory factory = new JsonFactory();
      ObjectMapper om = new ObjectMapper(factory);
      factory.setCodec(om);
      array = om.createArrayNode();

      Set<String> set = input.keySet();
      Iterator<String> iter = set.iterator();
      while (iter.hasNext()) {
        String key = iter.next().toString();
        array.add((ObjectNode) input.get(key));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "{\"result\":" + array.toString() + "}";
  }

  public static boolean isEnoughMemory() {
    try {
      Runtime runtime = Runtime.getRuntime();
      long freeMem = runtime.freeMemory() / 1024;
      String maxMemory = Play.application().configuration().getString("tz.maxMemory");
      if (freeMem < Integer.parseInt(maxMemory)) {
        Logger.error("Not Enough Memory!");
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

}
