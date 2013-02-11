package com.yasudanetwork.struts2.flush;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.mock.MockActionInvocation;

public class FlushInterceptorTest {

	@Test
	public void testSessionにstoreされることの確認() throws Exception {
		//sessionへの保存はActionContext経由で行われるので、ダミーのActionContextを用意する。
		Map<String, Object> contextMap = new HashMap<String, Object>();
		Map<String, Object> sessionMap = new HashMap<String, Object>();
		
		contextMap.put(ActionContext.SESSION, sessionMap);
		ActionContext context = new ActionContext(contextMap);
		ActionContext.setContext(context);
		
		DefaultActionInvocation invocation = new DefaultActionInvocation(null,false) {
			private static final long serialVersionUID = 1L;

			public Result createResult() {
				return new FlushResult();
			}
			public String invoke() throws Exception {
				return "success";
			}
		};
		SampleAction action = new SampleAction();
		
		FlushInterceptor interceptor = new FlushInterceptor();
		interceptor.intercept(invocation);
		
		assertThat(sessionMap.get("tokenkey"),notNullValue());
		
	}

}
