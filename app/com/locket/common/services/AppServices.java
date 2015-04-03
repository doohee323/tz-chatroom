package com.locket.common.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import play.Play;

import com.locket.session.models.User;

public class AppServices {
	private static final Integer cashadExpireHour = Play.application().configuration().getInt("cashad.expire.hour");

	public static void incrementUserCent(Long uid, String email, String isfb) {
		User user = User.find.byId(uid);
		user.cashAmount++;
		user.save();
	}
	
	
	public static String getCashAdsTimestamp(DateTime now) {
		String timestamp = "";
		if (now.getHourOfDay() < cashadExpireHour) {
			DateTime yesterday = now.minusDays(1);
			timestamp = yesterday.getYear() + "_" + yesterday.getMonthOfYear() + "_" + yesterday.getDayOfMonth() ;
		} else {
			timestamp = now.getYear() + "_" + now.getMonthOfYear() + "_" + now.getDayOfMonth() ;
		}
		
		return timestamp;
	}


	public static DateTime getCurrentTimeforTimezone(String tz) {
		DateTime now = null;
		try {
			now = new DateTime(System.currentTimeMillis(),
					DateTimeZone.forID(tz));
		} catch (Exception e) {
			now = new DateTime();
		}
		return now;
	}
}
