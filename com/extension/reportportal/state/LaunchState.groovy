package com.extension.reportportal.state

import com.extension.reportportal.context.ReportPortalContext
import com.extension.service.base.State

public class LaunchState extends State {
	private ReportPortalContext context
	public LaunchState(ReportPortalContext context) {
		this.context = context
	}

	public setItem(launchId) {
		this.context.currentLaunch = launchId
	}
	public getItem() {
		return this.context.currentLaunch
	}
	public finishItem() {
		this.context.currentLaunch = null
	}
}
