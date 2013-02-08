package com.yasudanetwork.struts2.flush.requestdeliver;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.yasudanetwork.struts2.flush.FlushResult;
import com.yasudanetwork.struts2.flush.FlushScopedRequest;

public class RequestParameterCopyToActionDeliverTest {

	@Test
	public void testDelivery() throws Exception {

		RequestParameterCopyToActionDeliver deliver = new RequestParameterCopyToActionDeliver();
		Map<String, Object> requestParams = new HashMap<String, Object> ();
		requestParams.put("orderId", "123"); //POST data.
		final SampleAction requestAction = new SampleAction();
		requestAction.setOrderId("app01-123"); //converted parameter.
		FlushResult result = new FlushResult();
		FlushScopedRequest param = new FlushScopedRequest(requestParams, requestAction,
				result);

		SampleAction responseAction = new SampleAction();

		deliver.delivery(param, responseAction);

		assertEquals("app01-123", responseAction.getOrderId());

	}



}
