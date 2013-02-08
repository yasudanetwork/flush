package com.yasudanetwork.struts2.flush.requestdeliver;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.NoParameters;
import com.opensymphony.xwork2.interceptor.ParameterNameAware;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.yasudanetwork.struts2.flush.FailureDeliveryRequestParametersException;
import com.yasudanetwork.struts2.flush.FlushScopedRequest;
import com.yasudanetwork.struts2.flush.RequestParametersDeliver;
/**
 * this work like parameter interceptor.
 *
 */
public class RequestParameterCopyToActionDeliver  implements RequestParametersDeliver{
	 private static final Logger LOG = LoggerFactory.getLogger(RequestParameterCopyToActionDeliver.class);
	   boolean ordered = false;
	    Set<Pattern> excludeParams = Collections.emptySet();
	    Set<Pattern> acceptParams = Collections.emptySet();
	    static boolean devMode = false;

	    private String acceptedParamNames = "[[\\p{Graph}\\s]&&[^,#:=]]*";
	    private Pattern acceptedPattern = Pattern.compile(acceptedParamNames);
	    
		public Object delivery(FlushScopedRequest requestParameters,  Object responseAction)
				throws Exception {
	        
	        if (!(responseAction instanceof NoParameters)) {
	        	setParameters(requestParameters.getRequestAction(), responseAction,requestParameters.getRequestParameters());
	        }
	        return responseAction;
	    }
		protected boolean acceptableName(String name) {
	        if (isAccepted(name) && !isExcluded(name)) {
	            return true;
	        }
	        return false;
		}


	    @Inject("devMode")
	    public static void setDevMode(String mode) {
	        devMode = "true".equals(mode);
	    }

	    public void setAcceptParamNames(String commaDelim) {
	        Collection<String> acceptPatterns = asCollection(commaDelim);
	        if (acceptPatterns != null) {
	            acceptParams = new HashSet<Pattern>();
	            for (String pattern : acceptPatterns) {
	                acceptParams.add(Pattern.compile(pattern));
	            }
	        }
	    }
	    /**
	     * Return a collection from the comma delimited String.
	     *
	     * @param commaDelim the comma delimited String.
	     * @return A collection from the comma delimited String. Returns <tt>null</tt> if the string is empty.
	     */
	    private Collection<String> asCollection(String commaDelim) {
	        if (commaDelim == null || commaDelim.trim().length() == 0) {
	            return null;
	        }
	        return TextParseUtil.commaDelimitedStringToSet(commaDelim);
	    }
	    /**
	     * Compares based on number of '.' characters (fewer is higher)
	     */
	    static final Comparator<String> rbCollator = new Comparator<String>() {
	        public int compare(String s1, String s2) {
	            int l1 = 0, l2 = 0;
	            for (int i = s1.length() - 1; i >= 0; i--) {
	                if (s1.charAt(i) == '.') l1++;
	            }
	            for (int i = s2.length() - 1; i >= 0; i--) {
	                if (s2.charAt(i) == '.') l2++;
	            }
	            return l1 < l2 ? -1 : (l2 < l1 ? 1 : s1.compareTo(s2));
	        }

	    };

	    protected boolean isAccepted(String paramName) {
	        if (!this.acceptParams.isEmpty()) {
	            for (Pattern pattern : acceptParams) {
	                Matcher matcher = pattern.matcher(paramName);
	                if (matcher.matches()) {
	                    return true;
	                }
	            }
	            return false;
	        } else
	            return acceptedPattern.matcher(paramName).matches();
	    }

	    protected boolean isExcluded(String paramName) {
	        if (!this.excludeParams.isEmpty()) {
	            for (Pattern pattern : excludeParams) {
	                Matcher matcher = pattern.matcher(paramName);
	                if (matcher.matches()) {
	                    return true;
	                }
	            }
	        }
	        return false;
	    }

	    /**
	     * Whether to order the parameters or not
	     *
	     * @return True to order
	     */
	    public boolean isOrdered() {
	        return ordered;
	    }

	    /**
	     * Set whether to order the parameters by object depth or not
	     *
	     * @param ordered True to order them
	     */
	    public void setOrdered(boolean ordered) {
	        this.ordered = ordered;
	    }

	    /**
	     * Gets a set of regular expressions of parameters to remove
	     * from the parameter map
	     *
	     * @return A set of compiled regular expression patterns
	     */
	    protected Set getExcludeParamsSet() {
	        return excludeParams;
	    }

	    /**
	     * Sets a comma-delimited list of regular expressions to match
	     * parameters that should be removed from the parameter map.
	     *
	     * @param commaDelim A comma-delimited list of regular expressions
	     */
	    public void setExcludeParams(String commaDelim) {
	        Collection<String> excludePatterns = asCollection(commaDelim);
	        if (excludePatterns != null) {
	            excludeParams = new HashSet<Pattern>();
	            for (String pattern : excludePatterns) {
	                excludeParams.add(Pattern.compile(pattern));
	            }
	        }
	    }

	    /**
	     * Gets an instance of the comparator to use for the ordered sorting.  Override this
	     * method to customize the ordering of the parameters as they are set to the
	     * action.
	     *
	     * @return A comparator to sort the parameters
	     */
	    protected Comparator<String> getOrderedComparator() {
	        return rbCollator;
	    }
	    

	    protected void setParameters(Object requestAction, Object responseAction, final Map<String, Object> parameters) throws FailureDeliveryRequestParametersException {
	        ParameterNameAware parameterNameAware = (requestAction instanceof ParameterNameAware)
	                ? (ParameterNameAware) requestAction : null;

	        Map<String, Object> params;
	        Map<String, Object> acceptableParameters;
	        if (ordered) {
	            params = new TreeMap<String, Object>(getOrderedComparator());
	            acceptableParameters = new TreeMap<String, Object>(getOrderedComparator());
	            params.putAll(parameters);
	        } else {
	            params = parameters;
	            acceptableParameters = new LinkedHashMap<String, Object>();
	        }

	        for (Map.Entry<String, Object> entry : params.entrySet()) {
	            String name = entry.getKey();

	            boolean acceptableName = acceptableName(name)
	                    && (parameterNameAware == null
	                    || parameterNameAware.acceptableParameterName(name));

	            if (acceptableName) {
	                acceptableParameters.put(name, entry.getValue());
	            }
	        }
	        for (Map.Entry<String, Object> entry : acceptableParameters.entrySet()) {
	            String name = entry.getKey();
	            Object value = null;
				try {
					value = PropertyUtils.getProperty(requestAction, name);
				} catch (IllegalAccessException e1) {
					handleException(requestAction, name, e1);
				} catch (InvocationTargetException e1) {
					handleException(requestAction, name, e1);
				} catch (NoSuchMethodException e1) {
					handleException(requestAction, name, e1);
				}
	            try {
	                org.apache.commons.beanutils.BeanUtils.setProperty(responseAction, name, value);
	            } catch (IllegalAccessException e) {
	            	handleException(requestAction, name, e);
				} catch (InvocationTargetException e) {
					handleException(requestAction, name, e);
				}
	        }
	    }
		private void handleException(Object requestAction, String name,
				Exception e1) throws FailureDeliveryRequestParametersException {
			if (devMode) {
			    String developerNotification = LocalizedTextUtil.findText(ParametersInterceptor.class, "devmode.notification", ActionContext.getContext().getLocale(), "Developer Notification:\n{0}", new Object[]{
			             "Unexpected Exception caught getting '" + name + "' on '" + requestAction.getClass() + ": " + e1.getMessage()
			    });
			    LOG.error(developerNotification);
			    
			}
			throw new FailureDeliveryRequestParametersException(e1);
		}




}
