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
import com.utils.JSONUtils

import internal.GlobalVariable


public class LaunchService extends BaseService{
	private projectName
	private launchName
	private launchMode
	private launchDescription
	private launchTags
	private launchId

	public LaunchService(launchName, projectName=GlobalVariable.RP_NAME, description='', mode='DEFAULT', tags=[]) {
		this.projectName = projectName
		this.launchName = launchName
		this.launchMode = mode
		this.launchDescription = description
		this.launchTags = tags
	}

	public startLaunch() {
		def res = WS.sendRequest(startLaunchRequest())
		this.logErrorResponse(res)
		return JSONUtils.getJSONObject(res)
	}

	public finishLaunch(launchId) {
		this.launchId = launchId
		def res = WS.sendRequest(finishLaunchRequest())
		this.logErrorResponse(res)
		return JSONUtils.getJSONObject(res)
	}

	public setLaunchId(launchId) {
		this.launchId = launchId
	}

	public getLaunchId() {
		return this.launchId
	}

	public getProjectName() {
		return this.projectName
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
		[	('mode'): this.launchMode,
			('name'): this.launchName,
			('start_time'): DateUtils.getISOCurrentDate('UTC'),
			('description'): this.launchDescription,
			('tags'): this.launchTags]
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
		[('end_time'): DateUtils.getISOCurrentDate('UTC')]
	})
	private Closure<RequestObject> finishLaunchRequest
}
