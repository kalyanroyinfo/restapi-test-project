package com.learming.kr.DataProvider;

import com.learming.kr.utility.XMLDataReader;
import org.testng.annotations.DataProvider;

/**
 * Created by kalyanroy on 16/09/21.
 */
public class TestDataProvider
{
	XMLDataReader xmlDataReader=XMLDataReader.getInstance();

	@DataProvider(name = "getAuthorization")
	public  Object[][] getAuthorization() {
		System.out.println("Method: getAuthorization");
		return xmlDataReader.readDataFromXML("API","getAuthorization");
	}
}
