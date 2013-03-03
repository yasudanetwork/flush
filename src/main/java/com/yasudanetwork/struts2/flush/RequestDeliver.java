package com.yasudanetwork.struts2.flush;

public interface RequestDeliver {
	/**
	 * implement your delivery logic.
	 * <pre>
	 *   user submit -> request action -> response redirect -> [this method] -> response action -> user receive response.
	 * </pre>
	 * @param request
	 * @param actionInvocation
	 * @param original response action
	 * @return modified response action object.
	 * @throws Exception
	 */
	Object delivery(FlushScopedRequest request, Object responseAction) throws Exception ;
}
