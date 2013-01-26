package com.yasudanetwork.struts2.flush;


public interface ExceptionHandler {

	void handle(Throwable e) throws FailureDeliveryRequestParametersException;

}
