package com.extension.reportportal.service

import com.extension.service.annotation.RequestBuilder
import com.extension.service.base.BaseService
import com.extension.service.helper.BodyType
import com.extension.service.helper.MethodType
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.utils.DateUtils

import internal.GlobalVariable


public class LoggingService extends BaseService{
	private logMessage
	private logLevel
	private logTime
	private testItemId
	private LaunchService launch

	public LoggingService(launch) {
		this.launch = launch
	}

	public logDebug(stepId, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		this.log(stepId, 'debug', message, time)
	}

	public logError(stepId, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		this.log(stepId, 'error', message, time)
	}

	public logInfo(stepId, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		this.log(stepId, 'info', message, time)
	}
	@Keyword
	private log(testItemId, level, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		this.testItemId = testItemId
		this.logLevel = level
		this.logMessage = message
		this.logTime = time
		def res = WS.sendRequest(createLogRequest())
		this.logErrorResponse(res)
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
			('time'): "${this.logTime}"
		]
	})
	private Closure<RequestObject> createLogRequest
}
