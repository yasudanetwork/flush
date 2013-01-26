package com.yasudanetwork.struts2.flush.result;

import com.yasudanetwork.struts2.flush.FlushResult;

public class NullFlushResult extends FlushResult {

	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == null || obj instanceof NullFlushResult);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new NullFlushResult();
	}

	@Override
	public String toString() {
		return "null";
	}

}
