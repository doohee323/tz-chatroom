package com.locket.common.controller;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

    public static Result index() {
      return redirect("/index.html");
    }
}
