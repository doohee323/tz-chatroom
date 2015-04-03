package com.locket.common.security;

import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import com.locket.common.services.MD5;

public class SecurityCheckForGet extends Action.Simple {
  public Result call(Http.Context ctx) throws Throwable {
    String scode = ctx.request().getQueryString("scode");
    String email = ctx.request().getQueryString("email");

    if (email == null || scode == null) {
      return null;
    }

    String hashstr = MD5.hash(email + "elttut_tfiws_norud_bocaj_omo");
    if (hashstr.equals(scode)) {
      Logger.error(" Security check passed ");
      return delegate.call(ctx);
    }
    return null;
  }
}
