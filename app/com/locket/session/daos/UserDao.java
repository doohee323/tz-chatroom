package com.locket.session.daos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import org.codehaus.jackson.annotate.JsonProperty;

import play.Logger;

import com.locket.session.models.User;

//@JsonSerialize(include=Inclusion.NON_NULL)
public class UserDao {

	public Long id;
	
	public String error = "0";
	
	public String gender;

	public String dob;

	@Column(name="unique_id")
	public String uniqueId;
	
	@JsonProperty("paypal_email")
	public String paypalEmail;

	@JsonProperty("total_cash")
	public Integer totalCash;

	@JsonProperty("cash_amount")
	public Integer cashAmount;

	@Column(name="external_balance")
	public Integer externalBalance;
	
	@Column(name="user_email")
	public String email;

	public String zipcode;

	@JsonProperty("seen_ads")
	public List<String> seeAds = new ArrayList<String>();

	public String  appVersion;

	public String  firstName;

	public String  lastName;

	public String  tier; 

    public String status;
    public String tagline;

    @Column(name="my_handle")
    public String myHandle;

    @Column(name="profile_image_path")
    public String profileImagePath;
    
    @Column(name="profile_image_id")
    public String profileImageId;
    
    @Column(name="number_of_my_followers")
    public Long numberOfMyFollowers;
    
    @Column(name="full_name")
    public String name;

    @Column(name="request_message")
    public String message;
    
    public String handle;
    
    public Boolean tos;
	
	public UserDao(User user) {
		tier = user.tier;
		email = user.email;
		totalCash = user.externalBalance + user.cashAmount;
		id = user.id;
		uniqueId = user.uniqueId+"";
        Logger.debug("This is unique id"+user.uniqueId);
		paypalEmail = user.paypalEmail;
		Logger.debug("This is unique id"+paypalEmail);
        cashAmount = user.cashAmount;
        Logger.debug("This is unique id"+cashAmount);
        externalBalance = user.externalBalance;
        Logger.debug("This is unique id"+user.uniqueId);
        appVersion = user.appVersion;
        Logger.debug("This is unique id"+user.uniqueId);
        try {
			if (user.udob!=null) {
				//Logger.error("DOB:" + user.udob);
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				dob = df.format(user.udob);
			}
		} catch (Exception e) {
			Logger.error("UDOB:" + user.udob);
			Logger.error("UserDao:UserDao:dob" + e.fillInStackTrace());
		}

		gender = user.gender;
		zipcode = user.zipcode;

	    status = user.status;
	    tagline = user.tagline;
	    myHandle = user.myHandle;
	    profileImagePath = user.profileImagePath;
	    profileImageId = user.profileImageId;
	    numberOfMyFollowers = user.numberOfMyFollowers;
	    tos = user.tos;
	}
	
	public UserDao(User user,String view) {
		id = user.id;
		Logger.debug("Test Id - "+id);
		firstName = user.firstName;
		lastName =  user.lastName;
		gender = user.gender;
		zipcode = user.zipcode;
		email = user.email;
		tier = user.tier;
		uniqueId = user.uniqueId+"";
        status = user.status;
        tagline = user.tagline;
        myHandle = user.myHandle;
        profileImagePath = user.profileImagePath;
        profileImageId = user.profileImageId;
        numberOfMyFollowers = user.numberOfMyFollowers;
        tos = user.tos;
		try {
			if (user.udob!=null) {
				//Logger.error("DOB:" + user.udob);
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				dob = df.format(user.udob);
			}
		} catch (Exception e) {
			Logger.error("UDOB:" + user.udob);
			Logger.error("UserDao:UserDao:dob" + e.fillInStackTrace());
		}

	}
}
