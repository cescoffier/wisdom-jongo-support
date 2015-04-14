package org.wisdom.jongo.sample;

import com.mongodb.DB;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.configuration.Configuration;
import org.wisdom.api.model.Crud;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Component
@Instantiate
public class JongoBridge {

    @Context
    private BundleContext context;

    @Requires
    DB db;

    @Requires
    ApplicationConfiguration configuration;

    private Map<Class, ServiceRegistration<Crud>> registrations = new HashMap<>();

    @Validate
    public void start() throws ClassNotFoundException {
        Configuration jongo =
                configuration.getConfiguration("jongo");
        if (jongo != null) {
            for (String s: jongo.getList("entities")) {
                Class clazz = load(s);
                JongoCRUDService crud = new JongoCRUDService(clazz, db);
                Hashtable<String, Object> props = new Hashtable<>();
                props.put(Crud.ENTITY_CLASS_PROPERTY, clazz);
                props.put(Crud.ENTITY_CLASSNAME_PROPERTY, s);
                ServiceRegistration reg = context.registerService(Crud.class, crud, props);
                registrations.put(clazz, reg);
            }
        }
    }

    @Invalidate
    public void stop() {
        for (ServiceRegistration registration : registrations.values()) {
            registration.unregister();
        }
    }

    private Class load(String classname) throws ClassNotFoundException {
        for (Bundle bundle : context.getBundles()) {
            if (bundle.getBundleId() == 0) {
                continue;
            }
            try {
                return bundle.loadClass(classname);
            } catch (ClassNotFoundException e) {
                // Next
            }
        }
        throw new ClassNotFoundException(classname);
    }

}
