package com.locket.common.services;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import play.Logger;
import play.Play;
import redis.clients.jedis.Jedis;

public class MyJedis {
	private static final Integer cashadExpireHour = Play.application().configuration().getInt("cashad.expire.hour");
	
	private static final String INVALID_BASEKEY = "tz.chatroom_ads_invalids_";

	private static int ONEHOUR = 60 * 60;
	private static int TWENTYFOURHOUR = ONEHOUR*24;
	
	private static String SEENADS_BASEKEY = "tz.chatroom_user_seenads_";

	public static List<String> getSeenAds(String uid) {
		//Jedis jedis = pool.getResource();
		Jedis jedis = RedisManager.getInstance().getJedis();
		Collection<String> seenads =  null;
		try {
			String seenAdsKey = SEENADS_BASEKEY + uid;
			seenads = jedis.hgetAll(seenAdsKey).values();
		}catch (Exception e){
			Logger.error("" + e.fillInStackTrace());
		}finally {
			//pool.returnResource(jedis);
			RedisManager.getInstance().returnJedis(jedis);
		}

		return new ArrayList<String>(seenads);
	}

	public static boolean seenIt(String uid,String adid) {
		//Jedis jedis = pool.getResource();
		Jedis jedis = RedisManager.getInstance().getJedis();
		boolean ret = false;
		try {
			String seenAdsKey = SEENADS_BASEKEY + uid;
			//Logger.error("REDIS LOOK UP KEY: " + seenAdsKey);
			String ad = jedis.hget(seenAdsKey, adid);
			ret =  ad != null;
		}catch(Exception e) {
			Logger.error("" + e.fillInStackTrace());
		}finally {
			//jedis.unwatch();
			RedisManager.getInstance().returnJedis(jedis);
			//pool.returnResource(jedis);	
		}
		return ret;
	}

	public static void markItSeen(String uid,String adid, DateTime now ) {
		String seenAdsKey = SEENADS_BASEKEY + uid;
		Jedis jedis = RedisManager.getInstance().getJedis();
		try {
			String ad = jedis.hget(seenAdsKey, adid);
			if (ad == null) {
				boolean exists = jedis.exists(seenAdsKey);
				// set the expiration on the key
				jedis.hset(seenAdsKey, adid, adid);
				if (!exists) {
					Interval interv = new Interval(now, now.plusDays(1).toDateMidnight());
					long seconds = interv.toDuration().getStandardSeconds();
					
//					Logger.error("=============================================");
//					Logger.error("now for the user local timezone is: " + now);
//					Logger.error("from now to midnight user time is " + interv.toDuration().getStandardHours() + " hours");
//					Logger.error("from now to midnight user time is " + interv.toDuration().getStandardMinutes() + " minutes");
//					Logger.error("from now to midnight user time is " + interv.toDuration().getStandardSeconds() + " seconds");
					
					Long expiretime = seconds + (cashadExpireHour*ONEHOUR);
					if (expiretime > TWENTYFOURHOUR) {
//						Logger.error("calculated time is more than 24 hours: " + expiretime);
						expiretime = expiretime - TWENTYFOURHOUR;
//						Logger.error("after subtracting 24 hours: " + expiretime);
					}
					
//					Logger.error("setting time out for " + seenAdsKey + " to " + expiretime + " current time is" + now);
					jedis.expire(seenAdsKey,expiretime.intValue());
//					Logger.error("=============================================");

				} 
			}
		}catch(Exception e) {
			Logger.error("" + e.fillInStackTrace());
		} finally {
			//pool.returnResource(jedis);	
			RedisManager.getInstance().returnJedis(jedis);
		}
	}

	public static Long getExpirationTime(String uid) {
		Jedis jedis = RedisManager.getInstance().getJedis();
		try {
			String seenAdsKey = SEENADS_BASEKEY + uid;
			return jedis.ttl(seenAdsKey);
		}catch(Exception e) {
			Logger.error("" + e.fillInStackTrace());
		} finally { 
			RedisManager.getInstance().returnJedis(jedis);
		}
		return -2L;
	}

	public static Boolean expire(String uid) {
		Jedis jedis = RedisManager.getInstance().getJedis();
		try {
			String seenAdsKey = SEENADS_BASEKEY + uid;
			jedis.del(seenAdsKey);
		}catch(Exception e) {
			Logger.error("" + e.fillInStackTrace());
		} finally { 
			RedisManager.getInstance().returnJedis(jedis);
		}
		return true;
	}

	public static List<String> getSeenAdsPlusInvalidAds(String uid, String tier) {
		List<String> seenads = MyJedis.getSeenAds(uid);
		List<String> invalidads = MyJedis.getInvalidAds();
		seenads.addAll(invalidads);
		
		//clear me 
		Logger.error("---------                        before getting Seen Ads");
		List<String> defaultseenads = new ArrayList<String>();
		
		if(tier.equals("P")){
		defaultseenads.add("1016");
		defaultseenads.add("311");
		defaultseenads.add("298");
		defaultseenads.add("312");
		defaultseenads.add("1000");
		defaultseenads.add("98");
		defaultseenads.add("99");
		seenads.addAll(defaultseenads);
		}
		Logger.error("-----------                       after getting Seen Ads");    
        return seenads;
	}

	public static List<String> getInvalidAds() {
		Jedis jedis = RedisManager.getInstance().getJedis();
		Collection<String> invalidAds =  null;
		try {
			String seenAdsKey = INVALID_BASEKEY;
			invalidAds = jedis.hgetAll(seenAdsKey).values();
		}catch (Exception e){
			Logger.error("" + e.fillInStackTrace());
		}finally {
			//pool.returnResource(jedis);
			RedisManager.getInstance().returnJedis(jedis);
		}
		return new ArrayList<String>(invalidAds);
	}	

}