package com.utils

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.internal.KeywordMain
import com.kms.katalon.core.logging.ErrorCollector
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

public class Verification {
	public static verifyMapEqual(Map<Object, Object> expected, Map<Object, Object> actual, failureHandling=FailureHandling.STOP_ON_FAILURE, ignore = []) {
		expected.each { key,value ->
			def isExcluded = key.toString() in ignore
			if (!isExcluded) {
				def message = "\n\nValue of element \"${key}\" not matching :\n\n-----Expected-----\n${key}:${expected[key]}\n\n-----Actual-----\n${key}:${actual[key]}"
				verifyCollectionEqual(expected[key], actual[key], message, failureHandling, ignore)
			}
		}
	}

	public static verifyListEqual(List<Object> expected, List<Object> actual, failureHandling=FailureHandling.STOP_ON_FAILURE, ignore = []) {
		verifyEqual(expected.size(), actual.size(), "\n**Expected** : ${expected}\n**Actual** : ${actual}\n\nSize not matching ${expected.size()} != ${actual.size()}", failureHandling)
		expected.eachWithIndex {item, index ->
			def message = "\n\nValue at index \"${index}\" not matching : \n\n-----Expected-----\n${expected[index]}\n\n-----Actual-----\n${actual[index]}"
			verifyCollectionEqual(expected[index], actual[index], message, failureHandling, ignore)
		}
	}


	private static verifyCollectionEqual(Object expected, Object actual, String message='', failureHandling=FailureHandling.STOP_ON_FAILURE, ignore = []) {
		if(expected instanceof List) {
			verifyListEqual(expected, actual, failureHandling, ignore)
		}
		else if(expected instanceof Map) {
			verifyMapEqual(expected, actual, failureHandling, ignore)
		}
		else {
			verifyEqual(expected, actual, message, failureHandling)
		}
	}

	public static verifyEqual(Object expected, Object actual, String message='', failureHandling=FailureHandling.STOP_ON_FAILURE) {
		try {
			def isEqual = WS.verifyEqual(expected, actual, failureHandling)
			if (!isEqual && !message.isEmpty()) {
				/*
				 * Add custom StepFailedException to Error collector when CONTINUE_ON_FAILURE
				 */
				def errors = []
				KeywordMain.stepFailed(message, failureHandling)
				errors.addAll(ErrorCollector.getCollector().getErrors())

				// Remove duplicate errors
				errors.removeAt(errors.size() - 2)
				ErrorCollector.getCollector().clearErrors()
				errors.each {
					ErrorCollector.getCollector().addError(it)
				}
			}
		}
		catch (StepFailedException e) {
			/*
			 * Throw custom StepFailedException when STOP_ON_FAILURE
			 */
			if (message.isEmpty()) {
				throw e
			}
			else {
				throw new StepFailedException(message)
			}
		}
	}
}
