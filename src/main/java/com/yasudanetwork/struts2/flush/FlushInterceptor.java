package com.yasudanetwork.struts2.flush;

import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.yasudanetwork.struts2.flush.actionconfig.ActionConfigHelper;
import com.yasudanetwork.struts2.flush.exceptionhandler.ThrowExceptionHandler;
import com.yasudanetwork.struts2.flush.request.NullFlushScopedRequest;
import com.yasudanetwork.struts2.flush.requestdeliver.RequestParameterCopyToActionDeliver;

public class FlushInterceptor extends AbstractInterceptor {
	private transient ActionConfigHelper configHelper = new ActionConfigHelper();
	private transient RequestDeliver requestParametersDeliver = new RequestParameterCopyToActionDeliver();
	private transient ExceptionHandler exceptionHandler = new ThrowExceptionHandler();

    @Inject
    public void setActionConfigHelper(ActionConfigHelper actionConfigHelper) {
        this.configHelper = actionConfigHelper;
    }
    @Inject
    public void setRequestParametersDeliver(RequestDeliver requestParametersDeliver) {
    	this.requestParametersDeliver = requestParametersDeliver;
    }
    @Inject
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    	this.exceptionHandler = exceptionHandler;
    }
	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = 1L;
    
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

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
		//FIXME
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
