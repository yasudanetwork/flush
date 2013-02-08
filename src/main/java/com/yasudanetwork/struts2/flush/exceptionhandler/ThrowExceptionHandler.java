package com.yasudanetwork.struts2.flush.exceptionhandler;

import com.yasudanetwork.struts2.flush.ExceptionHandler;
import com.yasudanetwork.struts2.flush.FailureDeliveryRequestParametersException;

/**
 * If delivery failed requests, this will throw an exception.
 * this is default handling logic.
 */
public class ThrowExceptionHandler implements ExceptionHandler  {

	public void handle(Throwable e) throws FailureDeliveryRequestParametersException {
		throw new FailureDeliveryRequestParametersException(e);
	}

}
