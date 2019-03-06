package com.extension.reportportal.listener

import com.extension.reportportal.context.ReportPortalContext
import com.extension.reportportal.context.ReportPortalContext.ReportPortalState
import com.extension.reportportal.service.LaunchService
import com.extension.reportportal.service.LoggingService
import com.extension.reportportal.service.TestItemService
import com.extension.service.base.BaseListener
import com.extension.service.helper.Constant
import com.kms.katalon.core.context.internal.ExecutionListenerEvent
import com.kms.katalon.core.context.internal.ExecutionListenerEventHandler
import com.kms.katalon.core.context.internal.InternalTestCaseContext




public class ReportPortalListener extends BaseListener implements ExecutionListenerEventHandler {
	private LaunchService launch
	private TestItemService testItem
	private LoggingService logging
	private ReportPortalContext context
	private Closure afterTestClosure = null

	public ReportPortalListener(launchName, suiteName) {
		this.launch = new LaunchService(launchName)
		this.testItem = new TestItemService(launch)
		this.logging = new LoggingService(launch)
		this.context = new ReportPortalContext()

		/*
		 * Start a launch within the constructor due to BEFORE_TEST_SUITE event is skipped
		 */
		def res = this.launch.startLaunch()
		this.context.setItem(res.id)
		// Start Suite
		res = this.testItem.startTestSuite(suiteName, this.context.currentLaunch)
		this.context.setState(ReportPortalState.SUITE)
		this.context.setItem(res.id)
	}


	@Override
	public void handleListenerEvent(ExecutionListenerEvent event, Object[] testParam) {
		this.handleExecutionEvent(event, testParam)
	}

	@Override
	public afterSuiteHandler(){
		this.executeAfterTestClosure()

		// Finish suite
		this.context.setState(ReportPortalState.SUITE)
		testItem.finishTestSuite(this.context.getItem())
		this.context.finishItem()

		// Finish launch
		this.context.setState(ReportPortalState.LAUNCH)
		launch.finishLaunch(this.context.getItem())
		this.context.finishItem()
	}

	@Override
	public beforeTestHandler(testInfo){
		this.executeAfterTestClosure()
		def res = testItem.startTestStep(this.getTestCaseName(testInfo), this.context.currentSuite,  this.context.currentLaunch,
				this.getTestCaseVariable(testInfo))
		this.context.setState(ReportPortalState.STEP)
		this.context.setItem(res.id)
	}

	@Override
	public afterTestHandler(testInfo){
		/*
		 * Use closure because if there are any requests sent inside this method will lead to incorrectness of the built-in suite results 
		 */
		this.context.setState(ReportPortalState.STEP)
		this.context.collectIssues()
		def stepId = this.context.getItem()
		afterTestClosure = {
			testItem.finishTestStep(stepId, this.getTestCaseStatus(testInfo))
			this.createErrorSteps(stepId)
			this.createLogSteps(stepId)
			this.createFullStackTraceLog(testInfo, stepId)
		}
		this.context.finishItem()
	}

	@Override
	public beforeStepHandler(stepInfo){
		this.executeAfterTestClosure()
		this.context.addLogStep(stepInfo)
	}

	@Override
	public afterStepHandler(){
		this.context.setState(ReportPortalState.STEP)
		if (!this.context.currentStep.isEmpty()) {
			this.createErrorSteps(this.context.getItem())
			this.createLogSteps(this.context.getItem())
		}
	}

	private executeAfterTestClosure() {
		if (afterTestClosure) {
			afterTestClosure.call()
			afterTestClosure = null
		}
	}


	private createFullStackTraceLog(InternalTestCaseContext testInfo, String stepId) {
		def testCaseStatus = testInfo.getTestCaseStatus()
		def logMessage = testInfo.getMessage()
		switch(testCaseStatus){
			case Constant.KATALON_FAIL_STATUS:
			case Constant.KATALON_ERROR_STATUS:
				this.logging.logError(stepId, "${logMessage}")
				break
			default:
				this.logging.logInfo(stepId, "${testInfo.getTestCaseId()} ${testCaseStatus}")
				break
		}
	}

	private createLogSteps(stepId) {
		this.context.logSteps.each {
			this.createDecriptionSteps(stepId,it['description'], it['time'])
			this.logging.logInfo(stepId, it['name'], it['time'])
		}
		this.context.logSteps.clear()
	}

	private createErrorSteps(stepId) {
		this.context.errors.each {
			def time = it['time']
			it['errors'].each {
				this.logging.logError(stepId, "[ERROR] " + it.toString() , time)
			}
		}
		this.context.errors.clear()
	}

	private createDecriptionSteps(String itemId, String description, String time) {
		if (!description.isEmpty()) {
			this.logging.logDebug(itemId, "[DESCRIPTION] " + description, time)
		}
	}

	private getTestCaseVariable(InternalTestCaseContext testCase) {
		return testCase.getTestCaseVariables()
	}

	private getTestCaseName(InternalTestCaseContext testCase) {
		def testName = testCase.getTestCaseId().toString().substring(testCase.getTestCaseId().toString().lastIndexOf("/") + 1)
		return testName
	}

	private getTestCaseStatus(InternalTestCaseContext testCase) {
		return testCase.getTestCaseStatus()
	}
}