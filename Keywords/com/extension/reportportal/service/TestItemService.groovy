package com.extension.reportportal.service

import com.extension.service.annotation.RequestBuilder
import com.extension.service.base.BaseService
import com.extension.service.helper.BodyType
import com.extension.service.helper.Constant
import com.extension.service.helper.MethodType
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.utils.DateUtils

import groovy.json.JsonSlurper
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
	private LaunchService launch

	public TestItemService(launch) {
		this.launch = launch
	}

	public startTestCase(testCaseName) {
		this.testCaseId = startTestItem(testCaseName, 'TEST')
	}

	public startTestStep(testStepName) {
		this.testStepId = startChildTestItem(testStepName, 'STEP', this.testCaseId)
	}

	public finishTestStep(status, issueComment='') {
		this.issueComment = issueComment
		this.finishTestItem(this.testStepId, this.convertStatus(status), this.convertIssueType(status))
		this.testStepId = ''
	}

	public finishTestCase(status='') {
		this.finishTestItem(this.testCaseId, this.convertStatus(status), this.convertIssueType(status))
		this.testCaseId = ''
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

	private startTestItem(itemName, itemType) {
		this.setTestItemName(itemName)
		this.setTestItemType(itemType)
		def res = WS.sendRequest(startTestItemRequest())
		return (new JsonSlurper()).parseText(res.getResponseText()).id
	}

	private startChildTestItem(itemName, itemType, parentId) {
		this.setTestItemName(itemName)
		this.setTestItemType(itemType)
		this.setTestItemParentId(parentId)
		def res = WS.sendRequest(startChildTestItemRequest())
		return (new JsonSlurper()).parseText(res.getResponseText()).id
	}

	private finishTestItem(testItemId, status, issueType) {
		this.setTestItemId(testItemId)
		this.testItemStatus = status
		this.issueType = issueType
		WS.sendRequest(finishTestItemRequest())
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

	private convertIssueType(status) {
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
			('start_time'): "${DateUtils.getISOCurrentDate()}",
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
			('start_time'): "${DateUtils.getISOCurrentDate()}",
			('type'): "${this.testItemType}"
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
		[('end_time'): "${DateUtils.getISOCurrentDate()}",
			('issue'): [('issue_type'): "${this.issueType}", ('comment') : "${this.issueComment}"],
			('status'): "${this.testItemStatus}"
		]
	})
	private Closure<RequestObject> finishTestItemRequest
}
