package com.atticoos.tiokhttp;

class InvalidMethodException extends Exception
{
	public InvalidMethodException (String method) {
		super("Invalid HTTP request method: " + method);
	}
}