package com.atticoos.tiokhttp;


import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.util.TiConfig;
import java.util.*;

@Kroll.proxy(creatableInModule=TitaniumOkhttpModule.class)
public class OkHttpClientProxy extends KrollProxy
{
	// Standard Debugging variables
	private static final String LCAT = "OkHttpClientProxy";
	private static final boolean DBG = TiConfig.LOGD;
	
	private HashMap<String, String> defaultHeaders = new HashMap<String, String>();

	public OkHttpClientProxy () {
		super();
	}
	
	@Kroll.method
	public void setDefaultHeaders (HashMap args) {
		Iterator it = args.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if (pairs.getKey() instanceof String && pairs.getValue() instanceof String) {
				this.defaultHeaders.put((String) pairs.getKey(), (String) pairs.getValue());
			}
		}
	}
	
	@Kroll.method
	public void GET (String url, HashMap args) {
		ProxyRequest request = new ProxyRequest(this, args, this.defaultHeaders);
		HttpClient.GET(url, request);
	}

	@Kroll.method
	public void POST (String url, HashMap args) {
		ProxyRequest request = new ProxyRequest(this, args, this.defaultHeaders);
		HttpClient.POST(url, request);
	}
	
	@Kroll.method
	public void PUT (String url, HashMap args) {
		ProxyRequest request = new ProxyRequest(this, args, this.defaultHeaders);
		HttpClient.PUT(url, request);
	}
	
	@Kroll.method
	public void PATCH (String url, HashMap args) {
		ProxyRequest request = new ProxyRequest(this, args, this.defaultHeaders);
		HttpClient.PATCH(url, request);
	}
	
	@Kroll.method
	public void DELETE (String url, HashMap args) {
		ProxyRequest request = new ProxyRequest(this, args, this.defaultHeaders);
		HttpClient.DELETE(url, request);
	}
	
	@Kroll.method
	public void HEAD (String url, HashMap args) {
		ProxyRequest request = new ProxyRequest(this, args, this.defaultHeaders);
		HttpClient.HEAD(url, request);
	}
}