package com.atticoos.tiokhttp;

import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.util.TiConfig;
import java.util.*;


@Kroll.proxy(creatableInModule=TitaniumOkhttpModule.class)
public class HttpClientProxy extends KrollProxy
{
	// Standard Debugging variables
	private static final String LCAT = "HttpClientProxy";

	private String requestMethod;
	private String requestUrl;
	private HashMap<String, String> requestHeaders = new HashMap<String, String>();
	private ProxyRequest request;

	public HttpClientProxy () {
		super();
	}

	@Kroll.getProperty @Kroll.method
	public String getResponseText () {
		if (this.request != null) {
			return this.request.getResponseText();
		} else {
			return null;
		}
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
	public void send (@Kroll.argument(optional = true) String data) {
		this.request = new ProxyRequest(this);
		this.request.setHeaders(this.requestHeaders);
		this.request.setPostData(data);

		Object onload = this.getProperty("onload");
		Object onerror = this.getProperty("onerror");

		if (onload != null && onload instanceof KrollFunction) {
			this.request.setSuccessCallback((KrollFunction) onload);
		}

		if (onerror != null && onerror instanceof KrollFunction) {
			this.request.setErrorCallback((KrollFunction) onerror);
		}

		try {
			HttpClient.method(this.requestMethod, this.requestUrl, this.request);
		} catch (InvalidMethodException e) {
			this.request.fireErrorCallback(e.toString());
		}
	}
}
