/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2015 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.wisdom.jongo.bridge;

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.configuration.Configuration;

import java.util.*;

/**
 * Ipojo component
 */
@Component(immediate = true)
@Instantiate
public class JongoEntityHub {

    private  static final  Logger LOGGER = LoggerFactory.getLogger(JongoEntityHub.class);

    private final ApplicationConfiguration configuration;

    @Requires(filter = "(factory.name=org.wisdom.jongo.bridge.JongoRepository)")
    Factory factory;

    private Map<String, ComponentInstance> instances = new HashMap<>();

    /**
     * Constructor.
     * @param configuration configuration file found in configuration/application.conf
     */
    public JongoEntityHub(@Requires ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Method that is called when the ipojo component starts. Reads the config file looking for jongo information.
     * Creates repository instances.
     * @throws MissingHandlerException
     * @throws UnacceptableConfiguration
     * @throws ConfigurationException
     */
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

    /**
     * Create the repository instance.
     * @param name the key in the config file used to identify the data source.
     * @param entities a list of entities that will use this repository.
     * @throws UnacceptableConfiguration
     * @throws MissingHandlerException
     * @throws ConfigurationException
     */
    private void createRepositoryInstance(String name, List<String> entities) throws UnacceptableConfiguration, MissingHandlerException,
            ConfigurationException {
        Dictionary<String, Object> conf = new Hashtable<>();
        conf.put("entities", entities);
        Dictionary<String, String> filter = new Hashtable<>();
        filter.put("database", "(|(name=" + name + ")(datasources= " + name + "))");
        conf.put("requires.filters", filter);
        instances.put(name, factory.createComponentInstance(conf));
    }

    /**
     * Called when the component is stopped, it clears all instances being used.
     */
    @Invalidate
    public void stop() {
        for (Map.Entry<String, ComponentInstance> entry : instances.entrySet()) {
            entry.getValue().dispose();
        }
        instances.clear();
    }

}
