package com.yasudanetwork.struts2.flush;

import com.opensymphony.xwork2.ActionInvocation;

public interface RequestParametersDeliver {
	/**
	 * implement your delivery logic.
	 * 
	 * @param createRequest
	 * @param actionInvocation
	 * @param responseAction
	 * @return modified response action object.
	 * @throws Exception
	 */
	Object delivery(FlushScopedRequest createRequest, ActionInvocation actionInvocation,Object responseAction) throws Exception ;
}
