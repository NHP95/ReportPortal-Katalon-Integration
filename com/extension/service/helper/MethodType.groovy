package com.extension.service.helper

enum MethodType {
	GET("GET"),POST("POST"),PUT("PUT"),DELETE("DELETE")

	private final String methodName;

	private MethodType(String methodName){
		this.methodName = methodName;
	}

	public String getMethodName() {
		return this.methodName;
	}
}
