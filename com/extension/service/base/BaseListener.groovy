package com.extension.service.base

import com.kms.katalon.core.context.internal.ExecutionListenerEvent

public abstract class BaseListener {
	public handleExecutionEvent(ExecutionListenerEvent event, Object[] testParam) {
		switch (event) {
			case ExecutionListenerEvent.AFTER_TEST_SUITE:
				afterSuiteHandler()
				break
			case ExecutionListenerEvent.BEFORE_TEST_CASE:
				beforeTestHandler(testParam[0])
				break
			case ExecutionListenerEvent.AFTER_TEST_CASE:
				afterTestHandler(testParam[0])
				break
			case ExecutionListenerEvent.BEFORE_TEST_STEP:
				beforeStepHandler(testParam)
				break
			case ExecutionListenerEvent.AFTER_TEST_STEP:
				afterStepHandler()
				break
			default:
				break
		}
	}

	public abstract afterSuiteHandler()
	public abstract beforeTestHandler(testInfo)
	public abstract afterTestHandler(testInfo)
	public abstract beforeStepHandler(stepInfo)
	public abstract afterStepHandler()
}
