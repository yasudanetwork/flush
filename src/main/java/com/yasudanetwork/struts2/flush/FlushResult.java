/*
 * MIT License
 */
package com.yasudanetwork.struts2.flush;

import org.apache.struts2.dispatcher.ServletRedirectResult;


/**
 * Requested POST parameter stored in flush scoped session.
 * And redirect requested URI with POST parameters.
 * Response was finished, remove flush scoped session data. 
 *
 */
public class FlushResult extends ServletRedirectResult {

    /**
	 * serial UID
	 */
	private static final long serialVersionUID = 1L;


}
