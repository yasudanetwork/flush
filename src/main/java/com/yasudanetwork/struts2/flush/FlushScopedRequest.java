package com.yasudanetwork.struts2.flush;

import java.util.Map;

public class FlushScopedRequest {

	private Map<String, Object> requestParameters;
	private Object requestAction;
	private FlushResult flushRequestParameters;
	public FlushScopedRequest(Map<String, Object> requestParameters,
			Object requestAction, FlushResult flushResult) {
		this.requestParameters = requestParameters;
		this.requestAction =requestAction;
		this.flushRequestParameters = flushResult;
	}
	public Map<String, Object> getRequestParameters() {
		return requestParameters;
	}
	public FlushResult getFlushRequestParameters() {
		return flushRequestParameters;
	}
	public Object getRequestAction() {
		return requestAction;
	}
	
}
