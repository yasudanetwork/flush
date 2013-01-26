package com.yasudanetwork.struts2.flush.requestdeliver;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Unchainable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
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
	public Object delivery(FlushScopedRequest request, ActionInvocation actionInvocation, Object responseAction) throws Exception {
        ValueStack stack = actionInvocation.getStack();
        CompoundRoot root = stack.getRoot();
        
        if (root.size() > 1) {
            List<CompoundRoot> list = new ArrayList<CompoundRoot>(root);
            list.remove(0);
            Collections.reverse(list);

            Map<String, Object> ctxMap = actionInvocation.getInvocationContext().getContextMap();
            Iterator<CompoundRoot> iterator = list.iterator();
            int index = 1; // starts with 1, 0 has been removed
            while (iterator.hasNext()) {
            	index = index + 1;
                Object o = iterator.next();
                if (o != null) {
                	if (!(o instanceof Unchainable)) {
                		reflectionProvider.copy(o, responseAction, ctxMap, excludes, includes);
                	}
                }
                else {
                	LOG.warn("compound root element at index "+index+" is null");
                }
            }
        }
        return responseAction;
	}
    
}
