package com.yasudanetwork.struts2.flush.exceptionhandler;



import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.yasudanetwork.struts2.flush.ExceptionHandler;
import com.yasudanetwork.struts2.flush.FailureDeliveryRequestParametersException;
/**
 * If delivery failed request, this wrote a error log silently.
 */
public class SilentExceptionHandler implements ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SilentExceptionHandler.class);

	public void handle(Throwable e)
			throws FailureDeliveryRequestParametersException {
		LOG.error("Failed to request delivery of flush scoped.", e);
	}

}
