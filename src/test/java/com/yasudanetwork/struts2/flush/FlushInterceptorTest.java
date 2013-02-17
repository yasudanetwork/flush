package com.yasudanetwork.struts2.flush;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
		
		//リクエストを処理した結果flushスコープで別のページにリダイレクトする結果になるようにActionInvocationを作る。
		DefaultActionInvocation invocation = new DefaultActionInvocation(null,false) {
			private static final long serialVersionUID = 1L;

			public Result createResult() {
				return new FlushResult();
			}
			public String invoke() throws Exception {
				return "success";
			}
		};
		
		FlushInterceptor interceptor = new FlushInterceptor();
		interceptor.intercept(invocation);

		//FlushScope専用トークンに何か入っていること
		assertThat(sessionMap.get("tokenkey"),notNullValue());
		
	}
	
	@Test
	public void testSessionにstoreされた内容が目的のページに復元されていることの確認() throws Exception {
		//sessionに情報がStoreされたActionContextを用意する。
		Map<String, Object> contextMap = new HashMap<String, Object>();
		Map<String, Object> sessionMap = new HashMap<String, Object>();
		contextMap.put(ActionContext.SESSION, sessionMap);
		ActionContext context = new ActionContext(contextMap);
		ActionContext.setContext(context);
		//sessionにダミーデータを入れる
		SampleAction requestAction = new SampleAction();
		requestAction.setOrderId("order 1");
		FlushResult result = new FlushResult();
		FlushScopedRequest request = new FlushScopedRequest(sessionMap, requestAction, result);
		sessionMap.put("tokenKey", request);
		
		
		//リクエストを処理した結果flushスコープで別のページにリダイレクトする結果になるようにActionInvocationを作る。
		DefaultActionInvocation invocation = new DefaultActionInvocation(null,false) {
			private static final long serialVersionUID = 1L;

			public Result createResult() {
				return result;
			}
			public String invoke() throws Exception {
				return "success";
			}
		};
		
		FlushInterceptor interceptor = new FlushInterceptor();
		interceptor.intercept(invocation);

		//FlushScope専用トークンはもう不要なのでクリアされていること
		
		assertThat(sessionMap.get("tokenkey"),nullValue());
		
	}

}
