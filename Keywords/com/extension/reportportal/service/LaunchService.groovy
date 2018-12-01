package com.extension.reportportal.service

import com.extension.service.annotation.RequestBuilder
import com.extension.service.base.BaseService
import com.extension.service.helper.BodyType
import com.extension.service.helper.MethodType
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.utils.DateUtils

import groovy.json.JsonSlurper
import internal.GlobalVariable


public class LaunchService extends BaseService{
	private projectName
	private launchName
	private launchMode
	private launchId

	public LaunchService(launchName, projectName=GlobalVariable.RP_NAME, mode='DEFAULT') {
		this.setProjectName(projectName)
		this.setLaunchName(launchName)
		this.setLaunchMode(mode)
	}

	public getLaunchId() {
		return this.launchId
	}

	public getProjectName() {
		return this.projectName
	}

	public startLaunch() {
		def res = WS.sendRequest(startLaunchRequest())
		launchId = (new JsonSlurper()).parseText(res.getResponseText()).id
	}

	public finishLaunch() {
		if (launchId)
			WS.sendRequest(finishLaunchRequest())
	}

	private setProjectName(projectName) {
		this.projectName = projectName
	}

	private setLaunchName(launchName) {
		this.launchName = launchName
	}

	private setLaunchId(launchId) {
		this.launchId = launchId
	}

	private setLaunchMode(launchMode) {
		this.launchMode = launchMode;
	}

	@RequestBuilder(requestType = {
		MethodType.POST
	},
	bodyType = {
		BodyType.TEXT_BODY
	},
	headers = {
		[new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
			new TestObjectProperty("Authorization", ConditionType.EQUALS, "${GlobalVariable.RP_TOKEN}")]
	},
	requestUrl = {
		"${GlobalVariable.RP_HOST}/${this.projectName}/launch"
	},
	bodyTextData = {
		[('mode'): this.launchMode,
			('name'): this.launchName,
			('start_time'): DateUtils.getISOCurrentDate()]
	})
	private Closure<RequestObject> startLaunchRequest

	@RequestBuilder(requestType = {
		MethodType.PUT
	},
	bodyType = {
		BodyType.TEXT_BODY
	},
	headers = {
		[new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
			new TestObjectProperty("Authorization", ConditionType.EQUALS, "${GlobalVariable.RP_TOKEN}")]
	},
	requestUrl = {
		"${GlobalVariable.RP_HOST}/${this.projectName}/launch/${this.launchId}/finish"
	},
	bodyTextData = {
		[('end_time'): DateUtils.getISOCurrentDate()]
	})
	private Closure<RequestObject> finishLaunchRequest
}
