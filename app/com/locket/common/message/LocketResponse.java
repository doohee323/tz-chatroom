package com.locket.common.message;

import org.codehaus.jackson.annotate.JsonProperty;

public class LocketResponse {
	public LocketResponse(String tag, int success, int error, String msg) {
		this.tag = tag;
		this.success = success;
		this.error = error;
		this.errorMessage=msg;
	}
	public String tag;
	public int 	success =0;
	public int 	error = 0;
	 
	@JsonProperty("error_msg")
	public String errorMessage = "";
}
