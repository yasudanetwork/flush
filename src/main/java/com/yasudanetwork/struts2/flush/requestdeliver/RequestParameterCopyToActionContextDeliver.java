package com.yasudanetwork.struts2.flush.requestdeliver;



import java.util.Collection;

import java.util.Map;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.yasudanetwork.struts2.flush.FlushScopedRequest;
import com.yasudanetwork.struts2.flush.RequestParametersDeliver;

/**
 * this work like chain mixed redirect.

 *
 */
public class RequestParameterCopyToActionContextDeliver implements RequestParametersDeliver {
	 private static final Logger LOG = LoggerFactory.getLogger(RequestParameterCopyToActionContextDeliver.class);	   
	 protected Collection<String> excludes;
	    protected Collection<String> includes;
	    
	    protected ReflectionProvider reflectionProvider;
	    
	    @Inject
	    public void setReflectionProvider(ReflectionProvider prov) {
	        this.reflectionProvider = prov;
	    }


	    /**
	     * Gets list of parameter names to exclude
	     *
	     * @return the exclude list
	     */
	    public Collection<String> getExcludes() {
	        return excludes;
	    }

	    /**
	     * Sets the list of parameter names to exclude from copying (all others will be included).
	     *
	     * @param excludes  the excludes list
	     */
	    public void setExcludes(Collection<String> excludes) {
	        this.excludes = excludes;
	    }

	    /**
	     * Gets list of parameter names to include
	     *
	     * @return the include list
	     */
	    public Collection<String> getIncludes() {
	        return includes;
	    }

	    /**
	     * Sets the list of parameter names to include when copying (all others will be excluded).
	     *
	     * @param includes  the includes list
	     */
	    public void setIncludes(Collection<String> includes) {
	        this.includes = includes;
	    }
	public Object delivery(FlushScopedRequest request, Object responseAction) throws Exception {
            Map<String, Object> ctxMap = request.getRequestParameters();
                		reflectionProvider.copy(request.getRequestAction(), responseAction, ctxMap, excludes, includes);
        return responseAction;
	}
    
}
