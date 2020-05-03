package com.extension.reportportal.service

import com.extension.service.annotation.RequestBuilder
import com.extension.service.base.BaseService
import com.extension.service.helper.BodyType
import com.extension.service.helper.Constant
import com.extension.service.helper.MethodType
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.RestRequestObjectBuilder
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.utils.DateUtils
import com.utils.JSONUtils

import groovy.json.JsonOutput
import internal.GlobalVariable


public class TestItemService extends BaseService{
	private testItemName
	private testItemType
	private issueType
	private issueComment
	private testItemStatus
	private testItemId
	private testCaseId
	private testStepId
	private testItemParentId
	private testParameter
	private LaunchService launch

	public TestItemService(launch) {
		this.launch = launch
	}

	public startTestSuite(suiteName, launchId) {
		return this.startTestItem(suiteName, 'SUITE', launchId)
	}

	public finishTestSuite(suiteId, status) {
		this.finishTestItem(suiteId, this.convertStatus(status))
	}

	public finishTestSuite(suiteId) {
		this.finishTestItem(suiteId)
	}

	public startTestCase(testCaseName, parentId, launchId) {
		return this.startChildTestItem(testCaseName, 'TEST', parentId, launchId)
	}

	public finishTestCase(testId, status) {
		this.finishTestItem(testId, this.convertStatus(status), this.getIssueType(status))
	}

	public finishTestCase(testId) {
		this.finishTestItem(testId)
	}

	public startTestStep(testStepName, parentId, launchId, parameters=null) {
		return this.startChildTestItem(testStepName, 'STEP', parentId, launchId, parameters)
	}

	public finishTestStep(stepId, status, issueComment) {
		this.issueComment = issueComment == null ? "" : issueComment
		this.finishTestItem(stepId, this.convertStatus(status), this.getIssueType(status))
	}

	public finishTestStep(stepId, status) {
		this.finishTestItem(stepId, this.convertStatus(status))
	}

	public getTestCaseId() {
		return this.testCaseId
	}

	public getTestStepId() {
		return this.testStepId
	}

	public getTestItemName() {
		return this.testItemName
	}

	public getTestItemParentId() {
		return this.testItemParentId
	}

	private startTestItem(itemName, itemType, launchId) {
		this.setTestItemName(itemName)
		this.setTestItemType(itemType)
		this.launch.setLaunchId(launchId)
		def res = WS.sendRequest(startTestItemRequest())
		this.logErrorResponse(res)
		return JSONUtils.getJSONObject(res)
	}

	private startChildTestItem(itemName, itemType, parentId, launchId, parameters) {
		this.setTestItemName(itemName)
		this.setTestItemType(itemType)
		this.setTestItemParentId(parentId)
		this.setTestParameter(parameters)
		this.launch.setLaunchId(launchId)
		def res = WS.sendRequest(startChildTestItemRequest())
		this.logErrorResponse(res)
		return JSONUtils.getJSONObject(res)
	}


	private finishTestItem(testItemId) {
		this.setTestItemId(testItemId)
		def res = WS.sendRequest(finishTestItemRequestWithAutoStatus())
		this.logErrorResponse(res)
		return JSONUtils.getJSONObject(res)
	}

	private finishTestItem(testItemId, status) {
		this.setTestItemId(testItemId)
		this.setTestItemStatus(status)
		def res = WS.sendRequest(finishTestItemRequestWithDefineStatus())
		this.logErrorResponse(res)
		return JSONUtils.getJSONObject(res)
	}

	private finishTestItem(testItemId, status, issueType) {
		this.setTestItemId(testItemId)
		this.setTestItemStatus(status)
		this.setIssueType(issueType)
		def res = WS.sendRequest(finishTestItemRequest())
		this.logErrorResponse(res)
		return JSONUtils.getJSONObject(res)
	}

	private sendTestItemRequest(String uri, MethodType methodType, Map payload) {
		String body = JsonOutput.toJson(payload)
		RequestObject testItemRequest = new RestRequestObjectBuilder()
				.withRestUrl(uri)
				.withHttpHeaders([
					new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json; charset=utf-8"),
					new TestObjectProperty("Authorization", ConditionType.EQUALS, "${GlobalVariable.RP_TOKEN}")
				])
				.withRestRequestMethod(methodType.getMethodName())
				.withTextBodyContent(body, "UTF-8")
				.build()
		def res = WS.sendRequest(testItemRequest)
		this.logErrorResponse(res)
		return JSONUtils.getJSONObject(res)
	}

	private setTestItemParentId(testItemParentId) {
		this.testItemParentId = testItemParentId
	}

	private setTestItemName(testItemName) {
		this.testItemName = testItemName
	}

	private setTestItemId(testItemId) {
		this.testItemId = testItemId
	}

	private setTestItemType(testItemType) {
		this.testItemType = testItemType
	}

	private setTestItemStatus(status) {
		this.testItemStatus = status
	}

	private setIssueType(issueType) {
		this.issueType = issueType
	}

	private setTestParameter(parameters){
		this.testParameter = this.convertMapToArray(parameters)
	}

	private convertMapToArray(parameters) {
		if (parameters) {
			List<Object> convertedParamaters = []
			parameters.each {key, value ->
				def parameter = [("key") : key.toString(), ("value") : value.toString()]
				convertedParamaters.add(parameter)
			}
			return convertedParamaters
		}
		else {
			return []
		}
	}

	private convertStatus(status) {
		/*
		 * In ReporPortal.io, issue is only attached to test item when status of the item is FAILED
		 * Plus, other status but FAILED does not highlight the error step
		 */
		switch (status) {
			case Constant.KATALON_PASS_STATUS:
				return Constant.REPORTPORTAL_PASS_STATUS
			case Constant.KATALON_FAIL_STATUS:
			case Constant.KATALON_ERROR_STATUS:
			default :
				return Constant.REPORTPORTAL_FAIL_STATUS
		}
	}

	private getIssueType(status) {
		switch (status) {
			case Constant.KATALON_PASS_STATUS:
				return Constant.REPORTPORTAL_NODEFECT_ISSUE_TYPE
			case Constant.KATALON_FAIL_STATUS:
				return Constant.REPORTPORTAL_PRODUCT_ISSUE_TYPE
			case Constant.KATALON_ERROR_STATUS:
			default :
				return Constant.REPORTPORTAL_TOINVESTIGATE_ISSUE_TYPE
		}
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
		"${GlobalVariable.RP_HOST}/${this.launch.getProjectName()}/item"
	},
	bodyTextData = {
		[('launch_id'): "${this.launch.getLaunchId()}",
			('name'): "${this.testItemName}",
			('start_time'): "${DateUtils.getISOCurrentDate('UTC')}",
			('type'): "${this.testItemType}"
		]
	})
	private Closure<RequestObject> startTestItemRequest

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
		"${GlobalVariable.RP_HOST}/${this.launch.getProjectName()}/item/${this.testItemParentId}"
	},
	bodyTextData = {
		[('launch_id'): "${this.launch.getLaunchId()}",
			('name'): "${this.testItemName}",
			('start_time'): "${DateUtils.getISOCurrentDate('UTC')}",
			('type'): "${this.testItemType}",
			('parameters') : this.testParameter
		]
	})
	private Closure<RequestObject> startChildTestItemRequest

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
		"${GlobalVariable.RP_HOST}/${this.launch.getProjectName()}/item/${this.testItemId}?"
	},
	bodyTextData = {
		[('end_time'): "${DateUtils.getISOCurrentDate('UTC')}",
			('issue'): [('issue_type'): "${this.issueType}", ('comment') : "${this.issueComment}"],
			('status'): "${this.testItemStatus}"
		]
	})
	private Closure<RequestObject> finishTestItemRequest

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
		"${GlobalVariable.RP_HOST}/${this.launch.getProjectName()}/item/${this.testItemId}?"
	},
	bodyTextData = {
		[('end_time'): "${DateUtils.getISOCurrentDate('UTC')}"
		]
	})
	private Closure<RequestObject> finishTestItemRequestWithAutoStatus


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
		"${GlobalVariable.RP_HOST}/${this.launch.getProjectName()}/item/${this.testItemId}?"
	},
	bodyTextData = {
		[('end_time'): "${DateUtils.getISOCurrentDate('UTC')}",
			('status'): "${this.testItemStatus}"
		]
	})
	private Closure<RequestObject> finishTestItemRequestWithDefineStatus
}
