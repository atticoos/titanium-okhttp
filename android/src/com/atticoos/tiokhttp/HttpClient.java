package com.atticoos.tiokhttp;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class HttpClient
{
	private static final String LCAT = "TitaniumHttpClient";

	public static void GET (String url, final ProxyRequest proxyRequest) {
		Request.Builder request = new Request.Builder().url(url);
		HttpClient.send(request, proxyRequest);
	}

	public static void POST (String url, final ProxyRequest proxyRequest) {
		RequestBody requestBody = RequestBody.create(proxyRequest.getMediaType(), proxyRequest.getPostData());
		Request.Builder request = new Request.Builder().url(url).post(requestBody);
		HttpClient.send(request, proxyRequest);
	}

	public static void PUT (String url, final ProxyRequest proxyRequest) {
		RequestBody requestBody = RequestBody.create(proxyRequest.getMediaType(), proxyRequest.getPostData());
		Request.Builder request = new Request.Builder().url(url).put(requestBody);
		HttpClient.send(request, proxyRequest);
	}

	public static void PATCH (String url, final ProxyRequest proxyRequest) {
		RequestBody requestBody = RequestBody.create(proxyRequest.getMediaType(), proxyRequest.getPostData());
		Request.Builder request = new Request.Builder().url(url).patch(requestBody);
		HttpClient.send(request, proxyRequest);
	}

	public static void DELETE (String url, final ProxyRequest proxyRequest) {
		Request.Builder request = new Request.Builder().url(url);
		if (proxyRequest.hasPostData()) {
			// workaround until https://github.com/square/okhttp/pull/1317
			RequestBody requestBody = RequestBody.create(proxyRequest.getMediaType(), proxyRequest.getPostData());
			request.method("DELETE", requestBody);
		} else {
			request.delete();
		}
		HttpClient.send(request,  proxyRequest);
	}

	public static void HEAD (String url, final ProxyRequest proxyRequest) {
		Request.Builder request = new Request.Builder().url(url).head();
		HttpClient.send(request, proxyRequest);
	}

	/**
	 * Send a request by supplying the request method as a String and uses reflection to execute the proper request
	 *
	 * @param methodString
	 * @param url
	 * @param proxyRequest
	 * @throws InvalidMethodException
	 */
	public static void method (String methodString, String url, final ProxyRequest proxyRequest) throws InvalidMethodException {
		try {
			methodString = methodString.toUpperCase().trim();
			Method method = HttpClient.class.getDeclaredMethod(methodString, String.class, ProxyRequest.class);
			method.invoke(null, url, proxyRequest);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidMethodException(methodString);
		}
	}

	private static void send (Request.Builder request, final ProxyRequest proxyRequest) {
		final OkHttpClient client = new OkHttpClient();

		if (proxyRequest.hasHeaders()) {
			Iterator it = proxyRequest.getHeaders().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
				request.header(pairs.getKey(), pairs.getValue());
			}
		}

		client.newCall(request.build()).enqueue(new Callback () {

			@Override
			public void onFailure (Request request, IOException e) {
				e.printStackTrace();
				if (proxyRequest.hasErrorCallback()) {
					proxyRequest.fireErrorCallback(null, null, null);
				}
			}

			@Override
			public void onResponse (Response response) {
				String bodyString;
				Headers responseHeaders = response.headers();
				HashMap<String, String> headerMap = new HashMap<String, String>();

				for (int i = 0; i < responseHeaders.size(); i++) {
					headerMap.put(responseHeaders.name(i), responseHeaders.value(i));
				}

				try {
					ResponseBody body = response.body();
					bodyString = body.string();
				} catch (IOException e) {
					bodyString = "";
				}

				if (response.isSuccessful() && proxyRequest.hasSuccessCallback()) {
					proxyRequest.fireSuccessCallback(bodyString, response.code(), headerMap);
				} else if (!response.isSuccessful() && proxyRequest.hasErrorCallback()) {
					proxyRequest.fireErrorCallback(bodyString, response.code(), headerMap);
				}
			}
		});
	}
}
