package com.learming.kr.testclasses;

import com.learming.kr.DataProvider.TestDataProvider;
import com.learming.kr.utility.RestAPIRequestBuilder;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kalyanroy on 16/09/21.
 */
public class TestNgCalling
{
	String baseUrl = "https://bookstore.toolsqa.com";

	RestAPIRequestBuilder restAPIRequestBuilder = RestAPIRequestBuilder.getInstance();

	@Test(dataProvider = "getAuthorization", dataProviderClass = TestDataProvider.class)
	public void getAuthToken(HashMap<String, String> testData){
		Map<String,String> headers=new HashMap<String, String>();
		headers.put("Content-Type","application/json");

		Map<String,String> params=new HashMap<String, String>();

		String requestBody="{ \"userName\":\"" + testData.get("userName") + "\", \"password\":\"" + testData.get("password") + "\"}";

		Response response= restAPIRequestBuilder.hitAPI(baseUrl, RestAssured.given(),
				Method.POST,testData.get("getAuthorizationAPIURL"),requestBody,headers,params,false );

		System.out.println(response.asString());

		String responseVal=response.asString();

		System.out.println(response.jsonPath().get("token"));
		System.out.println(response.path("token"));
	}
}
