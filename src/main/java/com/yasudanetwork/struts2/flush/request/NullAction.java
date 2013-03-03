package com.yasudanetwork.struts2.flush.request;

import com.opensymphony.xwork2.Action;

public class NullAction implements Action {

	public String execute() throws Exception {
		return Action.ERROR;
	}

}
