package org.wisdom.jongo.bridge;

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.configuration.Configuration;

import java.util.*;

@Component(immediate = true)
@Instantiate
public class JongoEntityHub {

    private final static Logger LOGGER = LoggerFactory.getLogger(JongoEntityHub.class);

    private final ApplicationConfiguration configuration;

    @Requires(filter = "(factory.name=org.wisdom.jongo.bridge.JongoRepository)")
    Factory factory;

    private Map<String, ComponentInstance> instances = new HashMap<>();

    public JongoEntityHub(@Requires ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    @Validate
    public void start() throws MissingHandlerException, UnacceptableConfiguration, ConfigurationException {
        // Parse the entities configuration
        Configuration jongoConfiguration = configuration.getConfiguration("jongo");
        if (jongoConfiguration == null) {
            LOGGER.warn("No jongo configuration");
            return;
        }

        for (String key : jongoConfiguration.asMap().keySet()) {
            // The key is the database name
            LOGGER.info("Create repository for {}", key);
            createRepositoryInstance(key, jongoConfiguration.getList(key + ".entities"));
        }
    }

    private void createRepositoryInstance(String name, List<String> entities) throws UnacceptableConfiguration, MissingHandlerException,
            ConfigurationException {
        Dictionary<String, Object> conf = new Hashtable<>();
        conf.put("entities", entities);
        Dictionary<String, String> filter = new Hashtable<>();
        filter.put("database", "(|(name=" + name + ")(datasources= " + name + "))");
        conf.put("requires.filters", filter);
        instances.put(name, factory.createComponentInstance(conf));
    }

    @Invalidate
    public void stop() {
        for (Map.Entry<String, ComponentInstance> entry : instances.entrySet()) {
            entry.getValue().dispose();
        }
        instances.clear();
    }

}
