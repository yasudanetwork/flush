package com.yasudanetwork.struts2.flush;

public class AlreadyExecutedResultException extends Exception {

	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyExecutedResultException(Throwable e) {
		super(e);
	}

}
