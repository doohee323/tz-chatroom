package com.locket.common.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import play.Logger;
import play.mvc.Http.Request;

public class AppUtil {

  public static String getFormParameter(Request request, String key) {
    try {
      return request.body().asFormUrlEncoded().get(key)[0];
    } catch (Exception e) {

    }
    return null;
  }

  public static void main(String args[]) {
    //getMd5Hash("1");
    AppUtil.getEncryptedLink(4231, false, false);
  }

  public static String getMd5Hash(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(input.getBytes());
      BigInteger number = new BigInteger(1, messageDigest);
      String md5 = number.toString(16);
      while (md5.length() < 32) {
        md5 = "0" + md5;
      }
      return md5;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
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

  public static <T> T getObjFromJson(Request request, T targetclass) {
    ObjectMapper mapper = new ObjectMapper();
    // JavaType type = mapper.getTypeFactory().
    // constructCollectionType(ArrayList.class, targetclass.getClass()) ;
    JavaType type = mapper.getTypeFactory().constructType(targetclass.getClass());
    try {
      return mapper.readValue(request.body().asJson(), type);
    } catch (JsonGenerationException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T> T getListFromJson(Request request, T targetclass) {
    ObjectMapper mapper = new ObjectMapper();
    JavaType type =
        mapper.getTypeFactory().constructCollectionType(ArrayList.class, targetclass.getClass());
    try {
      return mapper.readValue(request.body().asJson(), type);
    } catch (JsonGenerationException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getEncryptedLink(int id, Boolean engageBrowser, Boolean addz) {
    if (id == 0 || engageBrowser)
      return null;

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

      if (addz)

        uniqueNumber = uniqueNumber + "z";

      uniqueNumber = new StringBuilder(uniqueNumber).reverse().toString();

      return "http://locket.me/" + uniqueNumber;

    } catch (Exception e) {

      return null;

    }
  }
}