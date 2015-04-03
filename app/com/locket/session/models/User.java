package com.locket.session.models;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import play.Logger;
import play.db.ebean.Model;

import com.locket.session.forms.Registration;

/**
 * @author kaushik
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
@Entity
@Table(name = "USER")
public class User extends Model {

  private static final long serialVersionUID = 1L;

  public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

  public String firstName;

  public String lastName;

  public String answer;
  public String tier;

  @Id
  @Column(name = "user_id")
  public Long id;

  @JsonIgnore
  private String password;

  @JsonProperty("email")
  @Column(name = "user_email")
  public String email;

  public String getEmail() {
    return email;
  }

  @Column(name = "user_type")
  public String userType;

  @JsonProperty("cash_amount")
  @Column(name = "cash_amount")
  public Integer cashAmount;

  @Column(name = "user_name")
  public String username;

  public String dob;

  public Date udob;

  public String gender;

  public String appVersion;

  @Column(name = "zip")
  public String zipcode;

  @JsonProperty("paypal_email")
  @Column(name = "paypal_email")
  public String paypalEmail;

  @Column(name = "unique_url")
  public String uniqueId;

  @JsonProperty("created_at")
  @Column(name = "created_at")
  public Date createdAt;

  @Column(name = "tmp_pass")
  public String tmpPassword;

  @Column(name = "tmp_timestamp")
  public Date passwordExpires;

  @Column(name = "android_id")
  public String androidId;

  @Column(name = "referral_id")
  public String refererId;

  @Column(name = "active_user")
  public int activeUser;

  public String city;

  public String state;

  @Column(name = "facebook_id")
  public String facebookId;

  @Column(name = "external_balance")
  public Integer externalBalance = 0;

  public String manu;

  public String os;

  public String dma;

  public String latitude;
  public String longitude;

  public String user_group;

  public Date active_timestamp;

  public String status;
  public String tagline;

  @Column(name = "my_handle")
  public String myHandle;

  @Column(name = "profile_image_path")
  public String profileImagePath;

  @Column(name = "profile_image_id")
  public String profileImageId;

  @Column(name = "number_of_my_followers")
  public long numberOfMyFollowers;

  @Column(name = "full_name")
  public String name;

  @Column(name = "request_message")
  public String message;

  public String handle;

  public Boolean tos;

  public int type;

  public int getAge() {
    DateTime now = new DateTime();
    DateTime dateTime = new DateTime(udob);
    DateMidnight birthdate = new DateMidnight(dateTime);
    Years age = Years.yearsBetween(birthdate, now);
    return age.getYears();
  }

  public User(Registration filledForm) throws ParseException {
    Logger.error(" filledForm : " + filledForm);
    Logger.error(" Name : " + filledForm.name);
    this.name = filledForm.name;
    this.dob = filledForm.dob;
    this.udob = filledForm.udob;
    this.androidId = filledForm.androidId;
    this.password = filledForm.password;
    this.email = filledForm.email;
    this.user_group = "05";
    this.type = 0;
    if (this.email != null) {
      this.email = this.email.toLowerCase();
    }

    this.gender = filledForm.gender;
    if (this.gender.equalsIgnoreCase("male")) {
      this.gender = "M";
    } else if (this.gender.equalsIgnoreCase("female")) {
      this.gender = "F";
    }

    this.zipcode = filledForm.zipcode;

    this.manu = filledForm.manu;
    this.os = filledForm.os;
    this.appVersion = filledForm.appVersion;

    this.city = filledForm.city;
    this.state = filledForm.state;
    this.dma = filledForm.dma;
    if (this.state != null) {
      this.state = this.state.toUpperCase();
    }

    this.createdAt = new Date();
    this.activeUser = 1;
    this.cashAmount = 0;
    this.externalBalance = 0;
    this.facebookId = filledForm.facebookId;
    this.paypalEmail = "";
    this.answer = "";
    this.active_timestamp = new Date();
    this.appVersion = filledForm.appVersion;
    this.androidId = filledForm.androidId;
    this.latitude = filledForm.latitude;
    this.longitude = filledForm.longitude;
  }

  public User() {
  }

  public static User findByEmail(String email) {
    return find.where().eq("email", email).findUnique();
  }

  public static User findById(Long Id) {
    return find.where().eq("id", Id).findUnique();
  }

  public String getPassword() {
    return password;
  }

  public String toString() {
    return "email:" + email + '\n' + "password:" + password + '\n' + "androidId:" + androidId
        + '\n' + "dob:" + dob + '\n' + "gender:" + gender + '\n' + "zipcode:" + zipcode + '\n'
        + "latitude:" + latitude + '\n' + "longitude:" + longitude + '\n' + "city:" + city + '\n'
        + "state:" + state + '\n' + "udob:" + udob + '\n' + "appVersion:" + appVersion + '\n'
        + "facebookId:" + facebookId + '\n' + "os:" + os + '\n' + "manu:" + manu + '\n' + "dma:"
        + dma + '\n';
  }

  public static List<User> getAllCurators() {
    List<User> tempUsers = find.where().eq("status", "CASTER").findList();
    Collections.shuffle(tempUsers);
    return tempUsers;
  }

  public static List<User> getAllFeaturedCurators() {
    return find.where().eq("type", 1).eq("status", "CASTER").orderBy("number_of_my_casters")
        .findList();
  }
}
