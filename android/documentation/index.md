# titanium-okhttp
Titanium's native HTTP android module runs on an [Apache HTTP library from 2007](https://github.com/appcelerator/titanium_mobile/blob/58198c641d77e17d156431666e80bae732b5c130/android/titanium/src/thirdparty/org/apache/Commons-NOTICE.txt).
The problem with [Ti.Network.HTTPClient](http://docs.appcelerator.com/titanium/3.0/#!/api/Titanium.Network.HTTPClient) is it's lack of support for some HTTP methods, mainly **no PATCH support**.

`titanium-okhttp` introduces an interchangeable HTTP client that interfaces [okhttp](https://github.com/square/okhttp). As of Android 4.4, okhttp has been the supporting library. Let's get titanium up to speed!

Note to [gittio](http://gitt.io) users: this module is still in development

## Installation

#### With gittio
```
gittio install com.atticoos.tiokhttp
```

#### Without gittio
Download the latest zip in `android/dist` and unzip in your project root such that it fills/creates a `modules` directory with its contents

## BC Integration
You can seamlessly swap out your existing networking client for `titanium-okhttp`, and pick up where you left off. No refactoring.

```js
var client,
    options = {
	onload: function () {
		// handle your response
	},
	onerror: function () {
		// handle your error
	}
};

var client

if (Ti.Platform.osname === 'android') {
	client = require('com.atticoos.tiokhttp').createHttpClient(options);
} else {
	client = Ti.Network.createHttpClient(options);
}

client.setRequestHeader('Content-Type', 'application/json; charset=utf8');
client.open('GET', url);
client.send(data);
```

Nothing has changed, other than conditionally creating the client with a different library for android.

## A new interface
If you're starting a new project and don't care to have backward compatability, you'll find a much nicer request API:

An example `GET` request:
```js
var client = TitaniumOkHttp.createOkHttpClient();
client.GET(url, {
	onSuccess: function (data, status, headers) {
		// your response data
	}
});
```

An example `POST` request, with headers:
```js
var client = TitaniumOkHttp.createOkHttpClient({
	defaultHeaders: {
		'Content-Type': 'application/json'
	}
});
client.POST(url, {
	data: JSON.stringify({foo: 'bar'}),
	headers: {
		'Authorization': 'Access-Token xxx'
	},
	onSuccess: function (data, status, headers) {
		// your response data
	},
	onError: function (error, status, headers) {
		// your error response data
	}
});
```
