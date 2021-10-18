package com.learming.kr.utility;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class RestAPIRequestBuilder
{
	public static class RestAPIRequestBuilderModule {
		private static final RestAPIRequestBuilder instance = new RestAPIRequestBuilder();
	}

	public static RestAPIRequestBuilder getInstance() {
		return RestAPIRequestBuilderModule.instance;
	}

    public Response hitAPI(String baseUri,RequestSpecification specification,Method requestType, String url, String testdata,Map<String,String> header,Map<String,String> params, boolean isPathParam){
        Response res = null;
        specification.baseUri(baseUri);
		specification = addJsonBody(specification, testdata);
		specification = addheaders(specification, header);
		specification = addparams(specification, params, isPathParam);

		res=specification.request(requestType,url);
        return res;
    }

	private RequestSpecification addparams(RequestSpecification specification, Map<String, String> params, boolean isPathParam)
	{
		if(isPathParam){
			if(params!=null && !params.isEmpty())
				specification.pathParams(params);
		}else{
			if(params!=null && !params.isEmpty())
				specification.queryParams(params);
		}
		return specification;
	}

	private RequestSpecification addheaders(RequestSpecification specification, Map<String, String> header)
	{
		if(header!=null && !header.isEmpty())
			return specification.headers(header);
		else
			return specification;
	}

	private RequestSpecification addJsonBody(RequestSpecification specification, String testdata)
	{
		return specification.body(testdata);
	}
}
