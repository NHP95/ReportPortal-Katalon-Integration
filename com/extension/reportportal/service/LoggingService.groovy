package com.extension.reportportal.service

import com.extension.reportportal.context.ReportPortalContext
import com.extension.service.base.BaseService
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.RestRequestObjectBuilder
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.utils.DateUtils

import groovy.json.JsonOutput
import internal.GlobalVariable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

public class LoggingService extends BaseService{
	private LaunchService launch

	public LoggingService(launch) {
		this.launch = launch
	}

	public logDebug(stepId, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		this.log(stepId, 'debug', message, time)
	}

	public logError(stepId, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		this.log(stepId, 'error', message, time)
	}

	public logInfo(stepId, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		this.log(stepId, 'info', message, time)
	}

	public logScreenShot(stepId, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		/*
		 * Use external library to send this request since Katalon couldn't send such complex request
		 */
		OkHttpClient client = new OkHttpClient();
		String body =  """
				[{	"item_id" : "${stepId}",
					"level" : "info",
					"message" : "${message}",
					"time": "${time}",
					"file": {"name":"${stepId}"}}]
				"""
		if (ReportPortalContext.screenshots.size() > 0) {
			RequestBody requestBody = new MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("json_request_part", "json_request_part", RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
					.addFormDataPart("file", stepId, RequestBody.create(MediaType.parse("application/octet-stream"), ReportPortalContext.screenshots[0] as byte[]))
					.build()

			Request request = new Request.Builder()
					.header("Authorization", GlobalVariable.RP_TOKEN)
					.url("${GlobalVariable.RP_HOST}/${this.launch.getProjectName()}/log")
					.post(requestBody)
					.build()
			ReportPortalContext.screenshots.pop()
			Response response = client.newCall(request).execute()
			if (!response.isSuccessful()) {
				KeywordLogger log = new KeywordLogger()
				log.logWarning("Can't log screenshot : \n${response}")
			}
		}
	}

	@Keyword
	private log(testItemId, level, message='', time=DateUtils.getISOCurrentDate('UTC')) {
		/*
		 * Use the built-in request builder to prevent an undetermined encoding issue in Katalon 7.x.x version
		 */
		def payload = [('item_id'): "${testItemId}",
			('level'): "${level}",
			('message'): "${message}",
			('time'): "${time}"
		]
		String body = JsonOutput.toJson(payload)
		RequestObject logRequest = new RestRequestObjectBuilder()
				.withRestUrl("${GlobalVariable.RP_HOST}/${this.launch.getProjectName()}/log")
				.withHttpHeaders([
					new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json; charset=utf-8"),
					new TestObjectProperty("Authorization", ConditionType.EQUALS, "${GlobalVariable.RP_TOKEN}")
				])
				.withRestRequestMethod("POST")
				.withTextBodyContent(body, "UTF-8")
				.build()
		this.logErrorResponse(WS.sendRequest(logRequest))
	}
}
