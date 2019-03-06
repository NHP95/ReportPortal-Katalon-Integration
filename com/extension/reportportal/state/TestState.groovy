package com.extension.reportportal.state

import com.extension.reportportal.context.ReportPortalContext
import com.extension.service.base.State

public class TestState extends State {
	private ReportPortalContext context
	public TestState(ReportPortalContext context) {
		this.context = context
	}

	public setItem(testId) {
		this.context.currentTest.add(testId)
	}
	public getItem() {
		return this.context.currentTest[-1]
	}
	public finishItem() {
		this.context.currentTest.pop()
		this.context.foundErrors.clear()
		this.context.logSteps.clear()
	}
}
