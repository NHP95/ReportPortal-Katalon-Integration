package com.extension.service.helper

public enum BodyType {
	TEXT_BODY("TEXT_BODY"),FILE_BODY("FILE_BODY"),FORM_DATA_BODY("FORM_DATA_BODY"),URL_ENCODED_BODY("URL_ENCODED_BODY")
	private final String bodyName;

	private BodyType(String bodyName){
		this.bodyName = bodyName;
	}

	public String getMethodName() {
		return this.bodyName;
	}
}
