package com.extension.reportportal.state

import com.extension.reportportal.context.ReportPortalContext
import com.extension.service.base.State

public class StepState extends State {
	private ReportPortalContext context

	public StepState(ReportPortalContext context) {
		this.context = context
	}

	public setItem(stepId) {
		this.context.currentStep.add(stepId)
	}
	public getItem() {
		return this.context.currentStep[-1]
	}
	public finishItem() {
		this.context.currentStep.pop()
	}
}
