package com.yasudanetwork.struts2.flush;

import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.dispatcher.mapper.ActionMapping;


import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.DefaultActionProxy;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.yasudanetwork.struts2.flush.exceptionhandler.ThrowExceptionHandler;
import com.yasudanetwork.struts2.flush.request.NullFlushScopedRequest;
import com.yasudanetwork.struts2.flush.requestdeliver.RequestParameterCopyToActionDeliver;

public class FlushInterceptor extends AbstractInterceptor {

	RequestParametersDeliver requestParametersDeliver = new RequestParameterCopyToActionDeliver();
	ExceptionHandler exceptionHandler = new ThrowExceptionHandler();
	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = 1L;
    
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		/**
		invocation.addPreResultListener(new PreResultListener() {
            public void beforeResult(ActionInvocation invocation, String resultCode) {
        		try {
					if(isFlushScopeStart(invocation)) {
						storeRequestParametersToSession(createFlushScopedRequestParameters(invocation));
					}
				} catch (AlreadyExecutedResultException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        */
		if(this.isFlushScopeEnd(invocation)) {
			deliveryRequestParametersToResponseAction(invocation.getAction(), invocation,this.getStoredRequestParametersFromSession());
		}
		String resultCode = invocation.invoke();
		if(this.isFlushScopeStart(invocation, resultCode)) {
			this.storeRequestParametersToSession(createFlushScopedRequestParameters(invocation));
		}
		return resultCode;

	}

	private FlushScopedRequest createFlushScopedRequestParameters(
			ActionInvocation invocation) throws AlreadyExecutedResultException {
		Map<String,Object> requestParameters = ActionContext.getContext().getParameters();
		//find result settings of action.
		Result result = this.retrieveResult(invocation);
		if(result == null || !(result instanceof FlushResult)) {
			throw new IllegalStateException("excepted FlushResult(result type=\"flush\"). but unexpected result type. check \"type\" attribute of result settings in xml or annotation.");
		}
		return new FlushScopedRequest(requestParameters, invocation.getAction(),(FlushResult)result);
	}

	private void deliveryRequestParametersToResponseAction(Object responseAction, ActionInvocation actionInvocation,
			FlushScopedRequest storedRequestParametersFromSession) throws FailureDeliveryRequestParametersException{
		try {
			this.requestParametersDeliver.delivery(storedRequestParametersFromSession,responseAction);
		} catch(Exception e) {
			this.exceptionHandler.handle(e);
		}
		
	}


	private FlushScopedRequest getStoredRequestParametersFromSession() throws MissingFlushScopedRequestException {
		Map<String, Object> session = ActionContext.getContext().getSession();
        if (session == null) {
            return new NullFlushScopedRequest();
        }
        //TODO
        Object flushScopedRequest = session.get("tokenkey");
        if(flushScopedRequest == null || !(flushScopedRequest instanceof FlushScopedRequest)) {
        	throw new MissingFlushScopedRequestException();
        }
        return (FlushScopedRequest) flushScopedRequest;
	}






	private boolean isFlushScopeStart(ActionInvocation invocation,String resultCode) throws AlreadyExecutedResultException {
		//ignore ajax request.
		if(this.isAjaxRequest(invocation)) return false; 
		Result result = retrieveResult(invocation);
		if(result != null && result instanceof FlushResult) {
			return true;
		}
		return false;
	}
	private ResultConfig _retrieveResult(ActionInvocation actionInvocation,String resultCode) {
		ActionContext invocationContext = actionInvocation.getInvocationContext();
		
        ActionConfig config = actionInvocation.getProxy().getConfig();
        Map<String, ResultConfig> results = config.getResults();
        ResultConfig resultConfig = null;

        try {
            resultConfig = results.get(resultCode);
        } catch (NullPointerException e) {
            // swallow
        }
        
        if (resultConfig == null) {
            // If no result is found for the given resultCode, try to get a wildcard '*' match.
            resultConfig = results.get("*");
        }
		//find result settings of ActionContext.
		ActionMapping mapping = (ActionMapping) invocationContext.get(ServletActionContext.ACTION_MAPPING);
		//com.opensymphony.xwork2.dispatcher.PageContext pageContext = (PageContext) invocationContext.get(ServletActionContext.PAGE_CONTEXT);
		

		return resultConfig;
	}
	private Result retrieveResult(ActionInvocation actionInvocation) throws AlreadyExecutedResultException {
        if(actionInvocation instanceof DefaultActionInvocation) {
        	try {
				return ((DefaultActionInvocation)actionInvocation).createResult();
			} catch (Exception e) {
				throw new AlreadyExecutedResultException(e);
			}
        }
		return null;
	}
	private boolean isFlushScopeEnd(ActionInvocation invocation) {
		//ignore ajax request.
		if(this.isAjaxRequest(invocation)) return false; 
		Map<String, Object> session = ActionContext.getContext().getSession();
        if (session == null) {
        	return false;
        }
        //TODO
        return session.containsKey("tokenkey");
	}
	private boolean isAjaxRequest(ActionInvocation invocation) {
		Map<String, Object> requestParameters = invocation.getInvocationContext().getParameters();
		return false;
	}
	private void storeRequestParametersToSession(FlushScopedRequest flushScopedRequest) {
		Map<String, Object> session = ActionContext.getContext().getSession();
        if (session == null) {
            session = new SessionMap<String, Object>(ServletActionContext.getRequest());
            ActionContext.getContext().setSession(session);
        }
        synchronized (session) {
        	//FIXME read constant or user setting.
        	session.put("tokenkey", flushScopedRequest);
        }
	}

}
