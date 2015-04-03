package com.locket.common.security;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import com.locket.common.services.MD5;

public class SecurityCheckForPost extends Action.Simple  {
	public Result call(Http.Context ctx) throws Throwable {
		String scode = null;
		String email = null;
		try {
			scode = ctx.request().body().asFormUrlEncoded().get("scode")[0];
			email = ctx.request().body().asFormUrlEncoded().get("email")[0];
		}catch (Exception e) {
			return null;
		}
		String hashstr = MD5.hash(email + "elttut_tfiws_norud_bocaj_omo");
		
		if (hashstr.equals(scode)) {
			return delegate.call(ctx);
		}
		return null;

	}
}
