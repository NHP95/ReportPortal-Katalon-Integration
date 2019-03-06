package com.extension.service.annotation
import java.lang.annotation.ElementType
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target

import com.extension.service.helper.BodyType
import com.extension.service.helper.Constant
import com.extension.service.helper.MethodType
import com.kms.katalon.core.testobject.TestObjectProperty



@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD, ElementType.FIELD])
public @interface RequestBuilder {
	Class requestType() default {MethodType.GET}

	Class bodyType() default {BodyType.TEXT_BODY}

	Class requestUrl() default {"/"}

	Class headers() default {
		new ArrayList<TestObjectProperty>(Constant.DEFAULT_USER_AGENT_HEADER)
	}

	Class bodyTextData() default { new HashMap<String,String>() }

	Class requestParameter() default { new HashMap<String,String>() }
}
