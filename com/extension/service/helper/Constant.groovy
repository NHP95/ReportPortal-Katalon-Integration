package com.extension.service.helper

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

public class Constant {
	public static final String API_VERSION = "/api/v1";
	public static final String GOOGLE_BOT_AGENT= "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
	public static final TestObjectProperty DEFAULT_USER_AGENT_HEADER = new TestObjectProperty("User-Agent", ConditionType.EQUALS, Constant.GOOGLE_BOT_AGENT);
	public static final String KATALON_PASS_STATUS = "PASSED"
	public static final String KATALON_FAIL_STATUS = "FAILED"
	public static final String KATALON_ERROR_STATUS = "ERROR"
	public static final String KANOAH_PASS_STATUS = "Pass"
	public static final String KANOAH_FAIL_STATUS = "Fail"
	public static final String REPORTPORTAL_PASS_STATUS = "PASSED"
	public static final String REPORTPORTAL_FAIL_STATUS = "FAILED"
	public static final String REPORTPORTAL_NODEFECT_ISSUE_TYPE = "ND001"
	public static final String REPORTPORTAL_PRODUCT_ISSUE_TYPE = "PB001"
	public static final String REPORTPORTAL_TOINVESTIGATE_ISSUE_TYPE = "TI001"
	public static final String REPORTPORTAL_AUTOMATION_ISSUE_TYPE = "AB001"
	public static final String REPORTPORTAL_SYSTEM_ISSUE_TYPE = "SI001"
}
