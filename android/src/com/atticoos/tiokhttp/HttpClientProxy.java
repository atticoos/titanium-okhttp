package com.atticoos.tiokhttp;

import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.util.TiConfig;
import java.util.*;


@Kroll.proxy(creatableInModule=TitaniumOkhttpModule.class, propertyAccessors = {"onload", "onerror"})
public class HttpClientProxy extends KrollProxy
{
	// Standard Debugging variables
	private static final String LCAT = "HttpClientProxy";
	private static final boolean DBG = TiConfig.LOGD;
	
	private String requestMethod;
	private String requestUrl;
	private HashMap<String, String> requestHeaders = new HashMap<String, String>();
	
	private KrollFunction onload;
	private KrollFunction onerror;
	
	
	public HttpClientProxy () {
		super();
	}
	
	@Kroll.method
	public void open (String method, String url) {
		this.requestMethod = method;
		this.requestUrl = url;
	}
	
	@Kroll.method
	public void setRequestHeader (String key, String value) {
		this.requestHeaders.put(key, value);
	}
	
	@Kroll.method
	public void send (String data) {
		ProxyRequest request = new ProxyRequest(this);
		request.setHeaders(this.requestHeaders);
		request.setPostData(data);
		
		if (this.onload != null && this.onload instanceof KrollFunction) {
			request.setSuccessCallback(this.onload);
		}
		
		if (this.onerror != null && this.onerror instanceof KrollFunction) {
			request.setErrorCallback(this.onerror);
		}
		
		try {
			HttpClient.method(this.requestMethod, this.requestUrl, request);			
		} catch (InvalidMethodException e) {
			request.fireErrorCallback(e.toString());
		}
	}
	
	@Kroll.getProperty @Kroll.method
	public KrollFunction getOnload () {
		//String test = (String) this.getProperty("onload");
		//return test;
		return this.onload;
	}
	
	@Kroll.setProperty @Kroll.method
	public void setOnload (KrollFunction onload) {
		this.onload = onload;
	}
	
	@Kroll.method 
	public void test () {
		Log.d(LCAT, "TRYING TO INVOKE CALLBACK");
		if (this.onload != null && this.onload instanceof KrollFunction) {
			Log.d(LCAT, "ONLOAD IS NOT NULL");
			this.onload.call(this.getKrollObject(), new Object[]{"foobar"});
		}
		Log.d(LCAT, "WHY NO WORK? " + this.onload);
	}
}