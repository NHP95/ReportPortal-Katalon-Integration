package com.extension.service.base
import java.lang.annotation.Annotation
import java.lang.reflect.Field

import com.extension.service.annotation.RequestBuilder
import com.extension.service.builder.RestRequestBuilder
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject



public abstract class BaseService {
	public BaseService() {
		this.initialAllClosureFields();
	}

	private void initialAllClosureFields() {
		Class clazz = this.getClass()
		List<Field> fields = clazz.getDeclaredFields();
		for (Field field  in fields) {
			setValueToProxyField(field);
		}
	}


	private void setValueToProxyField(Field field) {
		List<Annotation> listAnnotations = field.getAnnotations();
		for(Annotation annotation in listAnnotations) {
			Boolean isCorrectRequestBuilderAnnotation = annotation.annotationType() == RequestBuilder.class
			if(isCorrectRequestBuilderAnnotation) {
				field.setAccessible(true);
				field.set(this,createClosureRequestObject(field));
			}
		}
	}

	private Closure<RequestObject> createClosureRequestObject(Field field) {
		RequestBuilder builderObject =  field.getAnnotation(RequestBuilder.class);
		Object bodyType = builderObject.bodyType().newInstance(this,this);
		Object methodType = builderObject.requestType().newInstance(this,this);
		Object requestUrl = builderObject.requestUrl().newInstance(this,this);
		Object headers = builderObject.headers().newInstance(this,this);
		Object requestParameter = builderObject.requestParameter().newInstance(this,this);
		Object bodyTextData = builderObject.bodyTextData().newInstance(this,this);

		return {
			RestRequestBuilder.getInstance()
					.Builder(methodType())
					.setRequestUrl(requestUrl())
					.setRequestBodyType(bodyType())
					.setRequestHeader(headers())
					.setRequestQueryParameter(requestParameter())
					.setRequestTextBodyData(bodyTextData())
					.build()
		}
	}
	
	protected logErrorResponse(ResponseObject res) {
		if (!(res.getStatusCode() in [200, 201])) {
			Class clazz = this.getClass()
			KeywordLogger log = new KeywordLogger()
			log.logWarning("${clazz.getName()} Can't create object on Reportportal :\n${res.getBodyContent().getText()} ")
		}
	}
}
