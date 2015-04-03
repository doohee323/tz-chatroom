package com.locket.session.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.validator.routines.EmailValidator;

import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

import com.avaje.ebean.Ebean;
import com.locket.common.message.LocketResponse;
import com.locket.common.security.SecurityCheckForGet;
import com.locket.common.security.SecurityCheckForPost;
import com.locket.common.util.AppUtil;
import com.locket.session.daos.UserDao;
import com.locket.session.forms.Registration;
import com.locket.session.models.Feedback;
import com.locket.session.models.User;

public class UserCtl extends Controller {

  private static Long CURRENT_NUMBER = Play.application().configuration().getLong("CURRENTNUMBER");
  private static final Integer TEN_DOOLARS = 10 * 100;
  private static final Integer FIVE_DOOLARS = 5 * 100;
  private static final String EMAIL_WELCOME_SUBJECT = "Welcome to Locket!";
  private static final String EMAIL_DONATION_SUBJECT = "Thank you for you donation!";
  private static final String EMAIL_CASHOUT_SUBJECT = "Your cash is on its way!";

  private static final String[][] numberGroup = { { "a", "b", "S", "T", "0", "1" },
      { "c", "d", "Q", "R", "2", "3" }, { "e", "f", "O", "P", "4", "5" },
      { "g", "h", "M", "N", "6", "7" }, { "i", "j", "K", "L", "8", "9" },
      { "k", "l", "I", "J", "U", "V" }, { "m", "n", "G", "H", "W", "X" },
      { "o", "p", "E", "F", "Y", "u" }, { "q", "r", "C", "D", "v", "w" },
      { "s", "t", "A", "B", "x", "y" } };

  @With(SecurityCheckForPost.class)
  public static Result login() {
    String email = null;
    String password = null;
    try {
      email = request().body().asFormUrlEncoded().get("email")[0];
      password = request().body().asFormUrlEncoded().get("password")[0];

      Logger.error("  EMAIL : " + email);
      Logger.error("  PASSWORD : " + password);

    } catch (Exception e) {
      return ok(Json.toJson(new LocketResponse("login", 0, 1, "invalid username or password")));
    }

    if (password.trim().length() == 0) {
      Logger.error("  PASSWORD : " + password.trim().length());
      return ok(Json.toJson(new LocketResponse("login", 0, 1, "invalid username or password")));
    }
    User user = User.findByEmail(email);
    if (user == null) {
      return ok(Json.toJson(new LocketResponse("login", 0, 1, "invalid username or password")));
    }
    Logger.error(" USER : " + user);

    if (user.getPassword().equals(password) == true) {
      updateUserInfo(user);
      UserDao userdao = new UserDao(user);
      // FIXME: MAKE SURE IT DOESNOT CRASH ON OLD VERSIONS
      // userdao.seeAds = MyJedis.getSeenAdsPlusInvalidAds(user.id + "",
      // user.tier);
      // Logger.error(" USER_DAO : " + userdao);
      return ok(Json.toJson(userdao));
    } else {
      return ok(Json.toJson(new LocketResponse("login", 0, 1, "invalid username or password")));
    }
  }

  static Form<Registration> regForm = Form.form(Registration.class);

  @With(SecurityCheckForPost.class)
  public static Result register() {
    Registration filledForm = regForm.bindFromRequest().get();
    if (regForm.hasErrors()) {
      return ok(Json.toJson(new LocketResponse("reg", 0, 1, "form contains error")));
    } else {
      User user = null;
      try {
        filledForm = filledForm.checkFormat();
        if (filledForm.getAge() < 13) {
          return ok(Json.toJson(new LocketResponse("reg", 0, 4,
              "you must be at least 13 years of age.")));
        }
        boolean isValidEmail = EmailValidator.getInstance().isValid(filledForm.email);
        if (isValidEmail == false) {
          return ok(Json.toJson(new LocketResponse("reg", 0, 5, "Email is not valid.")));
        }
        try {
          user = new User(filledForm);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (ParseException e) {
        e.printStackTrace();
        return ok(Json.toJson(new LocketResponse("reg", 0, 3,
            "date of birth is not formatted correctly")));
      }
      user.userType = "L";
      user.type = 0;
      user.tier = "";
      user.user_group = String.valueOf(1 + new Random().nextInt(2));
      Logger.error("----  Unique User created  " + user.user_group);

      try {
        if (!ifEmailExists(user.email)) {
          user.uniqueId = getUniqueId(CURRENT_NUMBER);
          user.status = "YET_TO_REQUEST";
          user.tagline = "";
          user.myHandle = "";
          user.tos = true;
          user.profileImageId = "";
          user.profileImagePath = "";
          user.numberOfMyFollowers = 0L;
          user.save();
        } else {
          return ok(Json.toJson(new LocketResponse("reg", 0, 2, "email already exists.")));
        }
      } catch (Exception e) {
        Logger.debug("Exception raised - " + e);
        return ok(Json.toJson(new LocketResponse("reg", 0, 2, "email already exists.")));
      }
      return ok(Json.toJson(new UserDao(user)));
    }
  }

  public static String getUniqueId(Long number) {
    try {
      String uniqueNumber = "";
      Random rand = new Random();
      number = number + 1;
      while (number > 0) {
        uniqueNumber = uniqueNumber + numberGroup[(int) (number % 10)][rand.nextInt(6)];
        number = number / 10;
      }
      return uniqueNumber;
    } catch (Exception e) {
      return number + "";
    }
  }

  @With(SecurityCheckForPost.class)
  public static Result fbconnect() {
    Registration filledForm = regForm.bindFromRequest().get();
    if (regForm.hasErrors()) {
      return ok(Json.toJson(new LocketResponse("reg", 0, 1, "form contains error")));
    } else {
      User user = User.findByEmail(filledForm.email);
      if (user == null) {
        User newuser = null;
        try {
          filledForm.checkFormat();
          if (filledForm.getAge() < 13) {
            return ok(Json.toJson(new LocketResponse("reg", 0, 4,
                "you must be at least 13 years of age.")));
          }

          boolean isValidEmail = EmailValidator.getInstance().isValid(filledForm.email);
          if (isValidEmail == false) {
            return ok(Json.toJson(new LocketResponse("reg", 0, 5, "Email is not valid.")));
          }

          newuser = new User(filledForm);
          newuser.userType = "F";
          newuser.type = 0;
          newuser.tier = "";
          newuser.user_group = String.valueOf(1 + new Random().nextInt(1));
          newuser.status = "YET_TO_REQUEST";
          newuser.tagline = "";
          newuser.tos = true;
          newuser.myHandle = "";
          newuser.profileImageId = "";
          newuser.profileImagePath =
              "http://graph.facebook.com/" + newuser.facebookId
                  + "/picture?type=large&width=200&height=200";
          newuser.numberOfMyFollowers = 0;

          try {
            if (!ifEmailExists(newuser.email)) {
              newuser.uniqueId = getUniqueId(CURRENT_NUMBER);
              newuser.save();
//              Mailer.sendEmailWelcome(newuser.email, EMAIL_WELCOME_SUBJECT);
            } else {
              return ok(Json.toJson(new LocketResponse("reg", 0, 2, "email already exists.")));
            }
            // logger.error("User after being saved:(fbconnect):" +
            // newuser.toString());

//            Mailer.sendEmailWelcome(newuser.email, EMAIL_WELCOME_SUBJECT);

          } catch (Exception e) {
            Logger.debug("This is the exception2 " + e);
            return ok(Json.toJson(new LocketResponse("reg", 0, 2, "email already exist")));
          }

          // Logger.error("returning:" + Json.toJson(new UserDao(newuser)));
          return ok(Json.toJson(new UserDao(newuser)));

        } catch (ParseException e) {
          // Logger.error("exception parse" + e.fillInStackTrace());
          return ok(Json.toJson(new LocketResponse("reg", 0, 3,
              "date of birth is not formatted correctly")));
        }
      } else {
        updateUserInfo(user);
        UserDao userdao = new UserDao(user);
        // userdao.seeAds = MyJedis.getSeenAdsPlusInvalidAds(user.id + "",
        // user.tier);
        return ok(Json.toJson(userdao));
      }
    }
  }


  @With(SecurityCheckForGet.class)
  public static Result update(Long uid, String key, String value) {
    checkEmailUidAuthenticity(uid);
    Ebean.createSqlUpdate("update USER set " + key + "='" + value + "' where user_id=" + uid)
        .execute();
    User user = User.find.byId(uid);
    return ok(Json.toJson(new UserDao(user)));
  }

  public static boolean ifEmailExists(String email) {
    User user = User.findByEmail(email);
    if (user == null) {
      Logger.error("Email is not in the database" + email);
      return false;
    } else {
      Logger.error("Email is already in the database" + email);
      return true;
    }
  }

  @With(SecurityCheckForGet.class)
  public static Result getUserId(String email) {
    User user = User.findByEmail(email);
    if (user == null) {
      return notFound("possible fradulant activity. adding to detction list");
    }
    return ok("{\"id\":" + user.id + "}");
  }

  @With(SecurityCheckForGet.class)
  public static Result fetchCash(Long uid) {
    try {
      checkEmailUidAuthenticity(uid);
      User user = User.find.byId(uid);
      if (user != null) {
        UserDao dao = new UserDao(user);
        return ok(Json.toJson(dao));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return notFound();

  }

  @With(SecurityCheckForGet.class)
  public static Result getUserInfo(Long uid) {
    checkEmailUidAuthenticity(uid);
    User user = User.find.byId(uid);
    if (user != null) {
      UserDao dao = new UserDao(user, "short");
      return ok(Json.toJson(dao));
    }
    return notFound();
  }

  @With(SecurityCheckForGet.class)
  public static Result curatorRequest(Long id, String name, String message, String handle,
      String latitude, String longitude) {
    User user = checkEmailUidAuthenticity(id);
    if (user != null) {
      try {
        user.name = name;
        user.message = message;
        user.myHandle = handle;
        user.latitude = latitude;
        user.longitude = longitude;
        user.active_timestamp = new Date();
        user.status = "PENDING_REQUEST";
        user.save();
        return ok(Json.toJson(new LocketResponse("Curator", 1, 0, " Successfully Saved ")));
      } catch (Exception e) {
        Logger.error("Exception in CuratorRequest - " + e);
        e.printStackTrace();
      }
      return ok(Json.toJson(new LocketResponse("Curator", 0, 1, " Unknown problem ")));
    }
    return notFound();
  }

  @With(SecurityCheckForGet.class)
  public static Result updateUserInfo(Long uid, String dob, String zip, String firstName,
      String lastName, String gender, Integer qid, String answer) {
    User user = checkEmailUidAuthenticity(uid);
    if (user != null) {
      user.dob = dob;
      try {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date udob = df.parse(dob);
        user.udob = udob;
      } catch (java.text.ParseException e) {
        try {
          DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
          Date udob = df.parse(dob);
          user.udob = udob;
        } catch (Exception ee) {
        }
      }
      if (user.getAge() < 13) {
        return ok(Json.toJson(new LocketResponse("reg", 0, 4,
            "you must be at least 13 years of age.")));
      }

      user.zipcode = zip;
      user.firstName = firstName;
      user.lastName = lastName;
      user.gender = gender;
      user.tier = "NA";
      user.active_timestamp = new Date();
      user.save();

//      UserQuestionAnswer qa = new UserQuestionAnswer(uid, qid, answer);
//      qa.save();

      UserDao dao = new UserDao(user, "short");
      return ok(Json.toJson(dao));
    }

    return notFound();
  }

  @With(SecurityCheckForGet.class)
  public static Result updateUserLocation(Long uid) {
    checkEmailUidAuthenticity(uid);
    User user = User.find.byId(uid);
    user.state = request().getQueryString("state");
    user.city = request().getQueryString("city");
    user.dma = request().getQueryString("dma");
    try {
      user.save();
    } catch (Exception e) {

    }
    return ok();
  }

  @With(SecurityCheckForGet.class)
  public static Result updateUserTOS(Long uid, Boolean tos) {
    checkEmailUidAuthenticity(uid);
    User user = User.find.byId(uid);
    user.tos = tos;
    try {
      user.save();
      return ok(Json.toJson(new LocketResponse("reg", 1, 0, "accepted TOS .")));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return ok(Json.toJson(new LocketResponse("reg", 0, 1, "rejected TOS .")));
  }

  @With(SecurityCheckForGet.class)
  private static void updateUserInfo(User user) {
    try {
      user.os = AppUtil.getFormParameter(request(), "os");
      user.manu = AppUtil.getFormParameter(request(), "manu");
      user.city = AppUtil.getFormParameter(request(), "city");
      user.state = AppUtil.getFormParameter(request(), "state");
      user.dma = AppUtil.getFormParameter(request(), "dma");
      user.androidId = AppUtil.getFormParameter(request(), "androidId");
      user.appVersion = AppUtil.getFormParameter(request(), "appVersion");

      if (user.androidId == null || user.androidId.trim().equals("")) {
        user.androidId = AppUtil.getFormParameter(request(), "android_id");
      }

      if (user.appVersion == null || user.appVersion.trim().equals("")) {
        user.appVersion = AppUtil.getFormParameter(request(), "app_version");
      }

      if (user.city != null) {
        user.city = user.city.toUpperCase();
      }

      if (user.state != null) {
        user.state = user.state.toUpperCase();
      }

      user.active_timestamp = new Date();
      user.save();
      Logger.error("User after being saved:(udpate user info):" + user.toString());

    } catch (Exception e) {
      Logger.error("could not update userinfo " + e.fillInStackTrace());
    }
  }

  private static User checkEmailUidAuthenticity(Long uid) {
    if (uid < 0) {
      throw new RuntimeException(
          "user email is not matching the provided uid, possible fradulant activity has been added to the list");
    }
    User user = User.find.byId(uid);
    String email = request().getQueryString("email");
    if (email.equalsIgnoreCase(user.email)) {
      return user;
    }
    throw new RuntimeException(
        "user email is not matching the provided uid, possible fradulant activity has been added to the list");
  }

  @With(SecurityCheckForGet.class)
  public static Result LocketFeedback(Long uid, String subject, String feedback) {
    try {
      new Feedback(uid, subject, feedback).save();
//      Mailer.sendEmailFeedback(request().getQueryString("email"), subject , feedback);    
    } catch (Exception e) {
      Logger.error(" error in LocketFeedback " + e);
    }
    return ok("");
  }
  
}
