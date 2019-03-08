package com.extension.reportportal.context

import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot

import com.extension.reportportal.state.LaunchState
import com.extension.reportportal.state.StepState
import com.extension.reportportal.state.SuiteState
import com.extension.reportportal.state.TestState
import com.extension.service.base.State
import com.extension.service.helper.Constant
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.logging.ErrorCollector
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.exception.BrowserNotOpenedException
import com.utils.DateUtils



public class ReportPortalContext {
	enum ReportPortalState{
		LAUNCH, SUITE, TEST, STEP
	}

	private State currentState
	public currentLaunch
	public currentSuite
	public List<Object> currentTest
	public List<Object> currentStep
	public List<String> foundErrors
	public List<String> errors
	public List<Object> logSteps
	public static List<Object> screenshots = []

	public ReportPortalContext() {
		this.currentLaunch = null
		this.currentTest = []
		this.currentStep = []
		this.currentState = new LaunchState(this)
		this.foundErrors = []
		this.logSteps = []
		this.errors = []
	}

	public setState(ReportPortalState state) {
		this.setCurrentState(state)
	}

	public setItem(itemInfo) {
		this.currentState.setItem(itemInfo)
	}

	public getItem() {
		return this.currentState.getItem()
	}

	public finishItem() {
		return this.currentState.finishItem()
	}

	private setCurrentState(ReportPortalState state) {
		switch(state) {
			case ReportPortalState.LAUNCH :
				this.currentState =  new LaunchState(this)
				break
			case ReportPortalState.SUITE :
				this.currentState =  new SuiteState(this)
				break
			case ReportPortalState.TEST :
				this.currentState = new TestState(this)
				break
			case ReportPortalState.STEP :
				this.currentState = new StepState(this)
				break
			default :
				throw new Exception("${state} is not yet implemented.")
		}
	}

	public addLogStep(stepInfo) {
		int STEP_NAME_INDEX = 2
		int STEP_ORDER_INDEX = 0
		int STEP_DESCRIPTION = 1
		this.collectIssues()
		this.foundErrors.addAll(this.getNewErrors())
		this.logSteps.add([
			('name') : "[STEP] " + stepInfo[STEP_NAME_INDEX],
			('order') : stepInfo[STEP_ORDER_INDEX],
			('status') : Constant.KATALON_PASS_STATUS,
			('time') : DateUtils.getISOCurrentDate('UTC'),
			('description') : stepInfo[STEP_DESCRIPTION],
			('errors') : []])
	}

	public collectIssues() {
		this.errors.add([('errors') : this.getNewErrors(), ('time') : DateUtils.getISOCurrentDate('UTC')])
	}
	
	@Keyword
	public static void captureScreenShotForReportportal() {
		try {
			def scrFile = ((TakesScreenshot)DriverFactory.getWebDriver()).getScreenshotAs(OutputType.BYTES);
			screenshots.add(scrFile)
		}
		catch (BrowserNotOpenedException e) {
			KeywordLogger log = new KeywordLogger()
			log.logWarning("Browser is not opened.")
		}
	}

	private getNewErrors() {
		return ErrorCollector.getCollector().getErrors() - this.foundErrors
	}
}
