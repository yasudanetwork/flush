package com.yasudanetwork.struts2.flush.requestdeliver;

public class SampleAction {
	private String orderId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String execute() {
		return "success";
	}
}
