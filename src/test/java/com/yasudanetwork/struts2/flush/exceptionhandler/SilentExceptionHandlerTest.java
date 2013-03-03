package com.yasudanetwork.struts2.flush.exceptionhandler;

import org.junit.Test;

import com.yasudanetwork.struts2.flush.FailureDeliveryRequestParametersException;

public class SilentExceptionHandlerTest {

	@Test
	public void testHandle() throws FailureDeliveryRequestParametersException {
		SilentExceptionHandler handler = new SilentExceptionHandler();
		handler.handle(new Exception());
		//success.
	}

}
