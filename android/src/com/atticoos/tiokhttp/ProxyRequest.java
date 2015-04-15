package com.atticoos.tiokhttp;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;

import com.squareup.okhttp.MediaType;

class ProxyRequest
{
	private final String KEY_ON_SUCCESS = "onSuccess";
	private final String KEY_ON_FAILURE = "onError";
	private final String KEY_POSTDATA = "data";
	private final String KEY_HEADERS = "headers";
	
	private KrollProxy proxy;
	private HashMap<String, String> headers;
	private String postData = new String(); // https://github.com/square/okhttp/pull/1558
	private String responseText;
	private KrollFunction successCallback;
	private KrollFunction errorCallback;
	
	public ProxyRequest(KrollProxy proxy) {
		this(proxy, null, null);
	}
	
	public ProxyRequest (KrollProxy proxy, HashMap<String, Object> proxyArgs) {
		this(proxy, proxyArgs, null);
	}
	
	public ProxyRequest (KrollProxy proxy, HashMap<String, Object> proxyArgs, HashMap<String, String> defaultHeaders) {
		this.proxy = proxy;
		
		if (defaultHeaders == null) {
			this.headers = new HashMap<String, String>();
		} else {
			this.headers = defaultHeaders;
		}
		
		if (proxyArgs != null) {
			Object object;
			if (proxyArgs.containsKey(KEY_ON_SUCCESS)) {
				object = proxyArgs.get(KEY_ON_SUCCESS);
				if (object instanceof KrollFunction) {
					this.successCallback = (KrollFunction) object;
				}
			}
			
			if (proxyArgs.containsKey(KEY_ON_FAILURE)) {
				object = proxyArgs.get(KEY_ON_FAILURE);
				if (object instanceof KrollFunction) {
					this.errorCallback = (KrollFunction) object;
				}
			}
			
			if (proxyArgs.containsKey(KEY_POSTDATA)) {
				object = proxyArgs.get(KEY_POSTDATA);
				if (object instanceof String) {
					this.setPostData((String) object);
				}
			}
			
			if (proxyArgs.containsKey(KEY_HEADERS)) {
				object = proxyArgs.get(KEY_HEADERS);
				if (object instanceof HashMap) {
					this.buildHeaders((HashMap) object);
				}
			}
		}
	}
	
	private void buildHeaders (HashMap headers) {
		Iterator it = headers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if (pairs.getKey() instanceof String && pairs.getValue() instanceof String) {
				this.headers.put((String) pairs.getKey(), (String) pairs.getValue());
			}
		}
	}
	
	public boolean hasHeaders () {
		return !this.headers.isEmpty();
	}
	
	public HashMap<String, String> getHeaders () {
		return this.headers;
	}
	
	public void setHeaders (HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}
	
	public boolean hasPostData () {
		return this.postData != null && !this.postData.isEmpty();
	}
	
	public String getPostData () {
		return this.postData;
	}
	
	public void setPostData (String postData) {
		// ensure the postData is never null, as OkHTTP may only consume non nulls at this time
		this.postData = (null == postData ? "" : postData);
	}
	
	public boolean hasContentType () {
		return this.headers.containsKey("Content-Type") || this.headers.containsKey("content-type");
	}
	
	public String getContentType () {
		if (this.headers.containsKey("Content-Type")) {
			return this.headers.get("Content-Type");
		} else if (this.headers.containsKey("content-type")) {
			return this.headers.get("content-type");
		} else {
			return null;
		}
	}
	
	public MediaType getMediaType () {
		if (this.hasContentType()) {
			return MediaType.parse(this.getContentType());
		} else {
			return MediaType.parse("");
		}
	}
	
	public boolean hasSuccessCallback () {
		return this.successCallback != null;
	}
	
	public void fireSuccessCallback (String responseData, Integer statusCode, HashMap headers) {
		this.responseText = responseData;
		this.successCallback.call(this.proxy.getKrollObject(), new Object[]{responseData, statusCode, headers});
	}
	
	public void setSuccessCallback (KrollFunction callback) {
		this.successCallback = callback;
	}
	
	public boolean hasErrorCallback () {
		return this.errorCallback != null;
	}
	
	public void fireErrorCallback (String responseData) {
		this.fireErrorCallback(responseData, null, null);
	}
	
	public void fireErrorCallback (String responseData, Integer statusCode, HashMap headers) {
		this.responseText = responseData;
		this.errorCallback.call(this.proxy.getKrollObject(), new Object[]{responseData, statusCode, headers});
	}
	
	public void setErrorCallback (KrollFunction callback) {
		this.errorCallback = callback;
	}
	
	public String getResponseText () {
		return this.responseText;
	}
}