package com.locket.common.services;

import java.util.HashMap;

public class Category {

	private static HashMap<String, String> catMap;


	static{
		catMap = new HashMap<String, String>();
		catMap.put("000", "Art"); 
		catMap.put("001", "Auto"); 
		catMap.put("002", "Business"); 
		catMap.put("003", "Car"); 
		catMap.put("004", "Education"); 
		catMap.put("005", "Family"); 
		catMap.put("006", "Health"); 
		catMap.put("007", "Food"); 
		catMap.put("008", "Hobby"); 
		catMap.put("009", "Home"); 
		catMap.put("010", "Law"); 
		catMap.put("011", "News"); 
		catMap.put("012", "Finance"); 
		catMap.put("013", "Society"); 
		catMap.put("014", "Science"); 
		catMap.put("015", "Pets"); 
		catMap.put("016", "Sports"); 
		catMap.put("017", "Fashion"); 
		catMap.put("018", "Technology"); 
		catMap.put("019", "Travel"); 
		catMap.put("020", "Real Estates"); 
		catMap.put("021", "Shopping"); 
		catMap.put("022", "Religion"); 
	}

	public static String getTagValue(String key){
		return catMap.get(key);
	}
}
