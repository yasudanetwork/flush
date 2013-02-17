package com.yasudanetwork.struts2.flush.actionconfig;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ResolverUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ActionConfigHelper {

    private Configuration configuration;

    @Inject
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
    
    public Set<String> getNamespaces() {
        Set<String> namespaces = Collections.emptySet();
        Map<String, Map<String, ActionConfig>>  allActionConfigs = configuration.getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            namespaces = allActionConfigs.keySet();
        }
        return namespaces;
    }

    public Set<String> getActionNames(String namespace) {
        Set<String> actionNames = Collections.emptySet();
        Map<String, Map<String, ActionConfig>> allActionConfigs = configuration.getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            Map<String, ActionConfig> actionMappings = allActionConfigs.get(namespace);
            if (actionMappings != null) {
                actionNames = actionMappings.keySet();
            }
        }
        return actionNames;
    }

    public ActionConfig getActionConfig(String namespace, String actionName) {
        ActionConfig config = null;
        Map<String, Map<String, ActionConfig>> allActionConfigs = configuration.getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            Map<String, ActionConfig> actionMappings = allActionConfigs.get(namespace);
            if (actionMappings != null) {
                config = actionMappings.get(actionName);
            }
        }
        return config;
    }
    
    public List<Properties> getJarProperties() throws IOException {
        ResolverUtil resolver = new ResolverUtil();
        List<Properties> poms = new ArrayList<Properties>();
        resolver.findNamedResource("pom.properties", "META-INF/maven");
        Set<URL> urls = resolver.getResources();
        for (URL url : urls) {
            Properties p = new Properties();
            p.load(url.openStream());
            poms.add(p);
        }
        return poms;
    }
}
