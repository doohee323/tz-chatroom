package com.locket.support.controllers;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.locket.support.daos.AppVersionDAO;

public class ApplicationCtl extends Controller {
	
	public static Result ping() {
		return ok();
	}
	
	public static Result version() {
	  AppVersionDAO version = new AppVersionDAO().get();
		return ok(Json.toJson(version));
	}
}
