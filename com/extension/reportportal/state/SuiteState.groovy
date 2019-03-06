package com.extension.reportportal.state

import com.extension.reportportal.context.ReportPortalContext
import com.extension.service.base.State

public class SuiteState extends State {
	private ReportPortalContext context
	public SuiteState(ReportPortalContext context) {
		this.context = context
	}

	public setItem(suiteId) {
		this.context.currentSuite = suiteId
	}
	public getItem() {
		return this.context.currentSuite
	}
	public finishItem() {
		this.context.currentSuite= null
	}
}
