package com.learming.kr.config;

import com.learming.kr.enums.Locations;

/**
 * Created by pk on 16/09/21.
 */
public class ConfigUtility
{
	private String xmlDataSet;

	public ConfigUtility()
	{
		xmlDataSet = Locations.TEST_DATA_LOCATION +  "data.xml";
	}

	public static class ConfigUtilityModule {
		private static final ConfigUtility instance = new ConfigUtility();
	}

	public static ConfigUtility getInstance() {
		return ConfigUtilityModule.instance;
	}

	public String getXmlDataSet() {
		return xmlDataSet;
	}
}
