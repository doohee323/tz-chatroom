package com.locket.support.daos;

import play.Play;

public class AppVersionDAO {

	private String versionold		= "";
	private String version 			= "";
	private String versionlatest 	= "";

	private AppVersionDAO appVersionInfo = null;
	
	//private AppVersion(){}
	
	public synchronized AppVersionDAO get() {
		if (appVersionInfo == null) {
			appVersionInfo = new AppVersionDAO();
			appVersionInfo.versionold		= Play.application().configuration().getString("app.versionold");
			appVersionInfo.version 		= Play.application().configuration().getString("app.version");
			appVersionInfo.versionlatest 	= Play.application().configuration().getString("app.versionlatest");
		}

		return appVersionInfo;
	}
	
	public String getVersionold(){
		return versionold;
	}
	
	public String getVersion(){
		return version;
	}

	public String getVersionlatest(){
		return versionlatest;
	}
}
