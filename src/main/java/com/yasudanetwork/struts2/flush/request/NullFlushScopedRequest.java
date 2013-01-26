package com.yasudanetwork.struts2.flush.request;

import java.util.Collections;
import java.util.Map;

import com.yasudanetwork.struts2.flush.FlushResult;
import com.yasudanetwork.struts2.flush.FlushScopedRequest;
import com.yasudanetwork.struts2.flush.result.NullFlushResult;

/**
 * this class is Null Special Value.
 *
 */
public class NullFlushScopedRequest extends FlushScopedRequest {

	public NullFlushScopedRequest() {
		super(null, null, null);
	}

	@Override
	public Map<String, Object> getRequestParameters() {
		return Collections.emptyMap();
	}

	@Override
	public FlushResult getFlushRequestParameters() {
		return new NullFlushResult();
	}

	@Override
	public Object getRequestAction() {
		return new NullAction();
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == null || obj instanceof NullFlushScopedRequest);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new NullFlushScopedRequest();
	}

	@Override
	public String toString() {
		return "null";
	}
}
