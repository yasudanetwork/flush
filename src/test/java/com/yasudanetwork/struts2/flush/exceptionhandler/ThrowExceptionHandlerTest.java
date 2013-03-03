package com.yasudanetwork.struts2.flush.exceptionhandler;

import static org.junit.Assert.*;

import org.junit.Test;

import com.yasudanetwork.struts2.flush.FailureDeliveryRequestParametersException;

public class ThrowExceptionHandlerTest {

	@Test
	public void testHandle() {
		ThrowExceptionHandler handler = new ThrowExceptionHandler();
		try {
			handler.handle(new Exception());
			fail();
		} catch (FailureDeliveryRequestParametersException e) {
			//success.
		}
		
	}

}
