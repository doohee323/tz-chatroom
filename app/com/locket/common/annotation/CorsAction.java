package com.locket.common.annotation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.RawBuffer;
import play.mvc.Http.Response;
import play.mvc.Result;

public class CorsAction extends Action.Simple {

  public Result call(Context context) throws Throwable {
    Response response = context.response();

    try {context.request().body().asJson();
      RawBuffer raw = context.request().body().asRaw();
      byte[] buf = raw.asBytes();
      InputStream requestStream = new ByteArrayInputStream(buf);
      BufferedReader reader = new BufferedReader(new InputStreamReader(requestStream)); //request.getInputStream()
      String result, line = reader.readLine();
      result = line;
      while((line=reader.readLine())!=null){
          result+=line;
      }
      System.out.println(result);
    } catch (Exception e) {
      System.out.println(e);
    }
    
//    response.setHeader("Access-Control-Allow-Origin", "*");

    // Handle preflight requests
//    if (context.request().method().equals("OPTIONS")) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with, sid, mycustom, smuser");
        response.setHeader("Access-Control-Max-Age", "1800");//30 min
//      return ok();
//    }

//    response.setHeader("Access-Control-Allow-Headers",
//        "X-Requested-With, Content-Type, X-Auth-Token");
    return delegate.call(context);
  }

}