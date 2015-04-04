package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Http.Request;

public class AppUtil {

  private static boolean isTest = false;

  public static final String BUNCH_SALT = "elttut_tfiws_norud_bocaj_omo";
  public static String TOKEN_VERSION = "1.0.11_1";

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

  public static void dmaParser() {
    try {
      BufferedReader br = new BufferedReader(new FileReader("dmas.txt"));
      try {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
          sb.append(line);
          sb.append('\n');
          line = br.readLine();

          String s = line;
          String[] columnDetail = s.split("\t", -1);
          System.out.println(columnDetail[0] + " code:" + columnDetail[1]);
        }

      } finally {
        br.close();
      }
    } catch (Exception e) {
      Logger.error(e.fillInStackTrace() + "");
    }
  }

  /**
   * This function can be tested.
   * */
  public static String getEncryptedLink(Long id) {
    final String[][] numberGroup =
        { { "a", "b", "S", "T", "0", "1" }, { "c", "d", "Q", "R", "2", "3" },
            { "e", "f", "O", "P", "4", "5" }, { "g", "h", "M", "N", "6", "7" },
            { "i", "j", "K", "L", "8", "9" }, { "k", "l", "I", "J", "U", "V" },
            { "m", "n", "G", "H", "W", "X" }, { "o", "p", "E", "F", "Y", "u" },
            { "q", "r", "C", "D", "v", "w" }, { "s", "t", "A", "B", "x", "y" } };
    try {
      String uniqueNumber = "";
      Random rand = new Random();
      while (id > 0) {
        uniqueNumber = uniqueNumber + numberGroup[(int) (id % 10)][rand.nextInt(6)];
        id = id / 10;
      }
      uniqueNumber = new StringBuilder(uniqueNumber).reverse().toString();
      return uniqueNumber;
    } catch (Exception e) {
      return null;
    }
  }

  public static int compareVersions(String curVer, String standVer) {
    if (curVer == null || curVer.equals(""))
      curVer = "0";
    curVer = curVer.replaceAll("_", ".");
    standVer = standVer.replaceAll("_", ".");
    if (curVer.equals(standVer))
      return 0;

    String[] curArry = curVer.split("\\.");
    String[] standArry = standVer.split("\\.");

    for (int i = 0; i < curArry.length; i++) {
      if (Integer.parseInt(curArry[i]) < Integer.parseInt(standArry[i])) {
        return -1;
      } else if (Integer.parseInt(curArry[i]) == Integer.parseInt(standArry[i])) {
      }
    }
    return 1;
  }

  public static boolean isTest() {
    return isTest;
  }

  public static void setTest(boolean isTest) {
    boolean aIsTest = Play.application().configuration().getBoolean("isTestMode");
    if (aIsTest) {
      AppUtil.isTest = isTest;
    }
  }

  public static void setTest(String isTest) {
    boolean aIsTest = Play.application().configuration().getBoolean("isTestMode");
    if (aIsTest && isTest != null) {
      AppUtil.isTest = Boolean.parseBoolean(isTest);
    }
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
        if (AppUtil.isTest() && request.body().asText() != null
            && request.body().asText().toString().indexOf("isTest") > -1) {
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
