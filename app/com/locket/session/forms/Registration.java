package com.locket.session.forms;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

public class Registration {

  public String name;

  public String email;

  public String password;

  public String androidId;

  public String dob;

  public String gender;

  public String zipcode;

  public String latitude;

  public String longitude;

  public String city;

  public String state;

  public Date udob;

  public String appVersion;

  public String facebookId;

  public String os;

  public String manu;

  public String dma;

  public int getAge() {
    DateTime now = new DateTime();
    DateTime dateTime = new DateTime(udob);
    DateMidnight birthdate = new DateMidnight(dateTime);
    Years age = Years.yearsBetween(birthdate, now);
    return age.getYears();
  }

  public Registration checkFormat() throws ParseException {

    try {
      DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
      Date udob = df.parse(dob);
      this.udob = udob;
    } catch (java.text.ParseException e) {
      try {
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        Date udob = df.parse(dob);
        this.udob = udob;
      } catch (Exception ee) {
        ee.printStackTrace();
      }
    } catch (Exception e) {
      try {
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        if (dob.equals(null)) {
        }
        Date udob = df.parse(dob);
        this.udob = udob;
      } catch (Exception ee) {
        ee.printStackTrace();
      }
    }

//    if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("m")) {
//      gender = "M";
//    }
//
//    if (gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("f")) {
//      gender = "F";
//    }
    
    
    if (email != null) {
      email = email.toLowerCase();
    }
    return this;
  }

  public String toString() {
    return "name:" + name + '\n' + "email:" + email + '\n' + "password:" + password + '\n'
        + "androidId:" + androidId + '\n' + "dob:" + dob + '\n' + "gender:" + gender + '\n'
        + "zipcode:" + zipcode + '\n' + "city:" + city + '\n' + "state:" + state + '\n' + "udob:"
        + udob + '\n' + "appVersion:" + appVersion + '\n' + "facebookId:" + facebookId + '\n'
        + "os:" + os + '\n' + "manu:" + manu + '\n' + "dma:" + dma + '\n';

  }
}
