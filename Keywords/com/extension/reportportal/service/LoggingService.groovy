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

import internal.GlobalVariable


public class LoggingService extends BaseService{
	private logMessage
	private logLevel
	private testItemId
	private LaunchService launch

	public LoggingService(launch) {
		this.launch = launch
	}

	public logError(stepId, message='') {
		this.log(stepId, 'error', message)
	}

	public logInfo(stepId, message='') {
		this.log(stepId, 'info', message)
	}

	private log(testItemId, level, message='') {
		this.testItemId = testItemId
		this.logLevel = level
		this.logMessage = message
		WS.sendRequest(createLogRequest())
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
		"${GlobalVariable.RP_HOST}/${this.launch.getProjectName()}/log"
	},
	bodyTextData = {
		[('item_id'): "${this.testItemId}",
			('level'): "${this.logLevel}",
			('message'): "${this.logMessage}",
			('time'): "${DateUtils.getISOCurrentDate()}"
		]
	})
	private Closure<RequestObject> createLogRequest
}
