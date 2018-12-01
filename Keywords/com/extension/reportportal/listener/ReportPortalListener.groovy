package com.extension.reportportal.listener

import com.extension.reportportal.service.LaunchService
import com.extension.reportportal.service.LoggingService
import com.extension.reportportal.service.TestItemService
import com.extension.service.base.BaseListener
import com.extension.service.helper.Constant
import com.kms.katalon.core.context.internal.ExecutionListenerEvent
import com.kms.katalon.core.context.internal.ExecutionListenerEventHandler
import com.kms.katalon.core.logging.ErrorCollector

public class ReportPortalListener extends BaseListener implements ExecutionListenerEventHandler{
	private LaunchService launch
	private TestItemService testItem
	private LoggingService logging
	private List<Object> steps = []
	private List<String> copiedErrors = []
	private stepInfo
	private final int PREVIOUS_STEP_INDEX = -1
	private final int STEP_ORDER_INDEX = 0
	private final int STEP_NAME_INDEX = 2
	private final int STEP_ORDER_DEFAULT_VALUE = -1
	private final int ITEM_NOT_FOUND_VALUE = -1

	public ReportPortalListener(suiteName) {
		launch = new LaunchService(suiteName)
		testItem = new TestItemService(launch)
		logging = new LoggingService(launch)
		launch.startLaunch()
	}

	@Override
	public void handleListenerEvent(ExecutionListenerEvent event, Object[] testParam) {
		this.handleExecutionEvent(event, testParam)
	}

	@Override
	public afterSuiteHandler(){
		launch.finishLaunch()
	}

	@Override
	public beforeTestHandler(testInfo){
		testItem.startTestCase(this.getTestCaseName(testInfo))
	}

	@Override
	public afterTestHandler(testInfo){
		this.collectRemainingErrors(this.getNewErrors())
		this.createSteps()
		testItem.finishTestCase(this.getTestCaseStatus(testInfo))
		steps.clear()
		copiedErrors.clear()
	}

	@Override
	public beforeStepHandler(stepInfo){
		if (steps)
			this.updateErrorStep(this.PREVIOUS_STEP_INDEX, this.getNewErrors())
		this.stepInfo = stepInfo
	}

	@Override
	public afterStepHandler(stepStatus=Constant.KATALON_PASS_STATUS){
		this.addStep(this.stepInfo[this.STEP_NAME_INDEX], stepStatus, this.stepInfo[this.STEP_ORDER_INDEX])
	}

	private createSteps() {
		steps.each {
			def issueComment = ''
			testItem.startTestStep(it['name'])
			it['errors'].each {
				logging.logError(testItem.getTestStepId(), it)
				issueComment += "* ${it} \n"
			}
			testItem.finishTestStep(it['status'], issueComment)
		}
	}

	private addStep(stepName, stepStatus, stepOrder=this.STEP_ORDER_DEFAULT_VALUE, errors=[]) {
		def stepInfo = [:]
		stepInfo.put("order", stepOrder)
		stepInfo.put("name", stepName)
		stepInfo.put("status", stepStatus)
		stepInfo.put("errors", errors)
		steps.add(stepInfo)
	}

	private updateErrorStep(index, errors=[]) {
		steps[index]["status"] = this.getFinalStatus(errors)
		steps[index]["errors"].addAll(errors)
		copiedErrors.addAll(errors)
	}

	private collectRemainingErrors(errors) {
		if (errors) {
			def lastVerifyStepIndex = steps.findLastIndexOf{ it["name"].toLowerCase() =~ /verify/}
			if (lastVerifyStepIndex != this.ITEM_NOT_FOUND_VALUE) {
				this.updateLastStepError(lastVerifyStepIndex, errors)
			}
			else {
				this.addStep("Unexpected error", this.getFinalStatus(errors), this.STEP_ORDER_DEFAULT_VALUE, errors)
			}
		}
	}

	private updateLastStepError(lastVerifyStepIndex, errors) {
		def indexToUpdate = lastVerifyStepIndex
		try {
			/*
			 * If the step followed by the last Verification step is a step in the test case (not in the listener or hooks)
			 * Then attach the issue to that step
			 */
			if (steps[lastVerifyStepIndex + 1]["order"] > 0) {
				indexToUpdate = lastVerifyStepIndex + 1
			}
		}
		catch(IndexOutOfBoundsException | NullPointerException e) {
			indexToUpdate = lastVerifyStepIndex
		}
		finally {
			this.updateErrorStep(indexToUpdate, errors)
		}
	}

	private getNewErrors() {
		return ErrorCollector.getCollector().getErrors() - this.copiedErrors
	}

	private getFinalStatus(errors=[]) {
		if (errors) {
			def hasFailedSteps = errors.findIndexOf{it.toString().contains("StepFailedException")} != this.ITEM_NOT_FOUND_VALUE
			return (hasFailedSteps)? Constant.KATALON_FAIL_STATUS : Constant.KATALON_ERROR_STATUS
		}
		return Constant.KATALON_PASS_STATUS
	}

	private getTestCaseName(testCase) {
		def testName = testCase.getTestCaseId().toString().substring(testCase.getTestCaseId().toString().lastIndexOf("/") + 1)
		def testVariables = (testCase.getTestCaseVariables())?" ${testCase.getTestCaseVariables()}" : ""
		return testName + testVariables
	}

	private getTestCaseStatus(testCase) {
		return testCase.getTestCaseStatus()
	}
}
