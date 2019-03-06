package com.utils

import com.kms.katalon.core.testobject.ResponseObject

import groovy.json.JsonException
import groovy.json.JsonSlurper

public class JSONUtils {
	private static parser

	private static getParser() {
		if (parser == null) {
			parser = new JsonSlurper()
		}
		return parser;
	}
	
	public static getMapFromJSONString(String jsonString) {
		return getParser().parseText(jsonString)
	}

	public static getJSONObject(ResponseObject res) {
		try {
			return getParser().parseText(res.getResponseText())
		}
		catch (IllegalArgumentException | JsonException e) {
			return null
		}
	}

	public static removeElementFromJsonArray(jsonArray, elementName=[]) {
		return jsonArray.each { json ->
			elementName.each { json.remove(it) }
		}
	}
}
