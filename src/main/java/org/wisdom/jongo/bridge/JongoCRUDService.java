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

import com.mongodb.DB;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.wisdom.api.model.*;
import org.wisdom.jongo.service.JongoCRUD;
import org.wisdom.jongo.service.MongoFilter;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.jongo.Oid.withOid;

/**
 * Jongo Crud service for the Wisdom-Framework. Extends the provided crud service.
 *
 * @param <T> the entity class that you wish to use the crud services with.
 * @param <K> The type of the id field found in the entity class. Ie String, Long, ObjectId.
 *            NOTE: Jongo seems to be limited to  6 types ids that it recognizes. Three are auto created ids by the database
 *            Case 1: field named _id of type String or long annotated with @ObjectId.
 *            Case 2: string or long with any name, annotated with both @ObjectId and @Id.
 *            Case 3: Type org.bson.types.ObjectId named _id.
 *            There are 3 type where you must manually set the key before saving to the database.
 *            Case 1: type long name id. No annotations.
 *            Case 2: type long annotated with @Id named whatever you want.
 *            Case 3: type string annotated with @Id named whatever you want.
 *            All other case are currently not supported.
 */
public class JongoCRUDService<T, K extends Serializable> implements JongoCRUD<T, K> {

    private final Class<T> entityClass;
    private final Class<K> entityKeyClass;
    private final MongoCollection collection;
    private final Field idField;
    private Class idFieldType;
    private JongoRepository repository;

    //constant name of the field in the entity to be used for id
    private static final String ID = "_id";

    /**
     * Constructor
     *
     * @param clazz the class using the service.
     * @param db    the database the service is connecting to.
     */
    public JongoCRUDService(Class<T> clazz, DB db) {
        this.entityClass = clazz;
        Jongo jongo = new Jongo(db);
        collection = jongo.getCollection(entityClass.getSimpleName());
        this.idField = findIdField();
        entityKeyClass = (Class<K>) this.idField.getType();
    }

    /**
     * Sets the repository to use.
     *
     * @param repository the repository we want to interact with.
     */
    public void setRepository(JongoRepository repository) {
        this.repository = repository;
    }

    /**
     * Sets the idField type.
     *
     * @param idFieldType the type of the id field.
     */
    public void setIdFieldType(Class idFieldType) {
        this.idFieldType = idFieldType;
    }

    /**
     * Get the value of the id field from an entity.
     * @param o the entity who's field we wish to access.
     * @return the value from the field.
     */
    private K getEntityId(T o) {
        try {
            if (!idField.isAccessible()) {
                idField.setAccessible(true);
            }
            return (K) idField.get(o);
        } catch (IllegalAccessException e) {
            // TODO LOGGER HERE
            return null;
        }
    }

    /**
     * Check the fields in the entity class and parent class to find the correct id field.
     *
     * @return returns the field that has the correct annotations.
     */
    private Field findIdField() {
        if (idField != null) {
            return idField;
        }

        //check all declared fields first
        for (Field field : entityClass.getDeclaredFields()) {
            if (isEntityId(field)) {
                return field;
            }
        }
        //If not found above check in the parent classes
        for (Field field : entityClass.getFields()) {
            if (isEntityId(field)) {
                return field;
            }
        }

        throw new IllegalStateException(
                "Cannot find the id field inside " + entityClass.getName());
    }

    /**
     * Check each field to see if it has the annotations we are looking for.
     *
     * @param field from an entity.
     * @return true if it has the correct annotations otherwise returns false. Assumes that there isn't more than
     * one field with correct annotations.
     */
    private boolean isEntityId(Field field) {
        Class type = field.getType();
        String name = field.getName();

        org.jongo.marshall.jackson.oid.ObjectId objectId =
                field.getAnnotation(org.jongo.marshall.jackson.oid.ObjectId.class);
        org.jongo.marshall.jackson.oid.Id id =
                field.getAnnotation(org.jongo.marshall.jackson.oid.Id.class);

        if (ID.equals(name) && objectId != null
                || id != null && objectId != null) {
            setIdFieldType(ObjectId.class);
            return true;
        }
        if (hasAnnotation(field, org.jongo.marshall.jackson.oid.Id.class)
                || type.equals(ObjectId.class)) {  // objectId is null
            setIdFieldType(type);
            return true;
        }
        if (id == null && objectId == null && ID.equals(name)) {
            setIdFieldType(type);
            return true;
        }

        return false;
    }

    /**
     * Check if the filed is annotated.
     *
     * @param field      field from current class or parent class.
     * @param annotation the annotation we are searching for.
     * @return true if found false if not.
     */
    private boolean hasAnnotation(Field field, Class annotation) {
        for (Annotation ann : field.getAnnotations()) {
            if (ann.annotationType().getName().equals(annotation.getName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the entity class that is using the database.
     *
     * @return the class.
     */
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * Get the type of the Id of the entity.
     *
     * @return type String.
     */
    @Override
    public Class<K> getIdClass() {
        return entityKeyClass;
    }

    /**
     * Save a new copy of the entity in the database if it doesn't not already exist. If the entity already exists
     * (i.e the same ID number) then it should update the existing copy.
     *
     * @param o the entity to save.
     * @return the updated entity with an id number.
     */
    @Override
    public T save(T o) {
        if (! o.getClass().equals(entityClass)) {
            // probably a super class
            o = createFromCustomConstructor(o);
        }
        WriteResult result = collection.save(o);
        if (result.getError() != null) {
            throw new RuntimeException("Cannot save instance " + o + " in " + collection.getName() + " : " + result.getLastError());
        } else {
            return o;
        }
    }

    private T createFromCustomConstructor(T o) {
        // Try to find a constructor that match the class of o
        try {
            Constructor<T> constructor = entityClass.getConstructor(o.getClass());
            if (! constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(o);
        } catch (NoSuchMethodException e) {
             throw new RuntimeException("The object " + o + " cannot be saved - incompatible type and no 'copy' " +
                     "constructor. The class " + entityClass.getName() + " requires a constructor accepting a " + o
                     .getClass().getName() + " has unique parameter.");
        } catch (InvocationTargetException e) {
            throw new RuntimeException("The object " + o + " cannot be saved - the constructor has thrown an " +
                    "exception", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("The object " + o + " cannot be saved - the class cannot be instantiated");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("The object " + o + " cannot be saved - unaccessible constructor");
        }
    }

    /**
     * Save a new copy of the entity in the iterable list if it doesn't exist, or updates if it does exists.
     *
     * @param iterable the collection of entities to be saved.
     * @return an iterable of the collections of entities that were saved.
     */
    @Override
    public Iterable<T> save(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T t : iterable) {
            list.add(save(t));
        }
        iterable = list;
        return iterable;
    }

    /**
     * Find an object from the database by it's unique Id number.
     *
     * @param id the unique id of the object.
     * @return the object if it exists, otherwise return null.
     */
    @Override
    public T findOne(K id) {

        if (id == null) {
            return null;
        }

        if (idFieldType.equals(ObjectId.class)) {
            String oid = id.toString();
            if (ObjectId.isValid(oid)) {
                return collection.findOne(withOid(id.toString())).as(entityClass);
            } else {
                return null;
            }
        }

        if (idFieldType.equals(String.class) || idFieldType.equals(Long.class) || idFieldType.equals(Long.TYPE)) {
            return collection.findOne(createIdQuery(id)).as(entityClass);
        }

        throw new IllegalArgumentException("Id of type '" + id + "' is not supported");
    }

    /**
     * Find one entity using the Mongo filter which gives us access to mongo query string formats.
     *
     * @param filter what we are searching for.
     * @return the einity if found otherwise returns null.
     */
    @Override
    public T findOne(EntityFilter<T> filter) {
        if (filter instanceof MongoFilter) {
            final MongoFilter dbFilter = (MongoFilter) filter;
            return collection.findOne(dbFilter.getFilter(),
                    dbFilter.getParams()).as(entityClass);
        } else {
            for (T entity : findAll()) {
                if (filter.accept(entity)) {
                    return entity;
                }
            }
        }
        return null;
    }

    /**
     * Find all of the objects in a Mongo Collection.
     *
     * @return an iterable of the entity type.
     */
    @Override
    public Iterable<T> findAll() {
        return collection.find().as(entityClass);
    }

    @Override
    public Iterable<T> findAll(Iterable<K> iterable) {
        List<T> entities = new ArrayList<>();
        for (K key : iterable) {
            T entity = findOne(key);
            if (entity == null) {
                throw new IllegalArgumentException("Cannot find an entity of type " + entityClass + " with id " + key);
            }
            entities.add(entity);
        }
        return entities;
    }

    /**
     * Find all of the objects in a Mongo Collection using a filter.
     *
     * @param filter what we want to search for.
     * @return an iterable of the entity type.
     */
    @Override
    public Iterable<T> findAll(EntityFilter<T> filter) {
        if (filter instanceof MongoFilter) {
            final MongoFilter dbFilter = (MongoFilter) filter;
            return collection.find(dbFilter.getFilter(),
                    dbFilter.getParams()).as(entityClass);
        } else {
            List<T> entities = new ArrayList<>();
            for (T entity : findAll()) {
                if (filter.accept(entity)) {
                    entities.add(entity);
                }
            }
            return entities;
        }
    }

    /**
     * Delete an object by  from the collection if it exists.
     *
     * @param id of the object you wish to delete for.
     *           If the id doesn't exist there is an IllegalArgumentException.
     *           <p>
     *           Note: as far as I can tell jongo only supports remove for object id types and not others.
     */
    @Override
    public void delete(K id) {
        WriteResult result;
        if (idFieldType.equals(ObjectId.class)) {
            result = collection.remove(withOid(String.valueOf(id)));
        } else {
            result = collection.remove(createIdQuery(id));

        }
        //get n is number of docs effected by operation in mongo
        if (result.getN() == 0) {
            throw new IllegalArgumentException("Unable to delete Id '" + id + "' not found");
        }
    }

    /**
     * Delete an object by  from the collection if it exists.
     *
     * @param o is an object that is an entity. It needs to have a valid _id field.
     * @return returns the original object passed in.
     * If the id doesn't exist there is an IllegalArgumentException.
     */
    @Override
    public T delete(T o) {
        K id = getEntityId(o);
        delete(id);
        return o;
    }


    /**
     * Delete a list of objects in the form of iterable from the collection if they exist.
     *
     * @param iterable is an iterable of entities.
     * @return the original iterable.
     */
    @Override
    public Iterable<T> delete(Iterable<T> iterable) {
        for (T temp : iterable) {
            delete(temp);
        }
        return iterable;
    }

    /**
     * Method provided by jongo to delete everything in the collection. Use with caution.
     */
    public void deleteAllFromCollection() {
        collection.remove();
    }

    /**
     * Checks to see if the object exists in the Mongo Collection based on its ID.
     *
     * @param id of the object to search for.
     * @return true if found false if not found.
     */
    @Override
    public boolean exists(K id) {
        return findOne(id) != null;
    }


    /**
     * Count the number of objects that are of the entity type in a Mongo Collection.
     *
     * @return count as type Long.
     */
    @Override
    public long count() {
        return collection.count();
    }

    private String createIdQuery(K id) {
        //for ids that are of type string
        if (idFieldType.equals(String.class)) {
            return "{" + ID + " : '" + id + "'}";
        }
        //for ids that are of type long
        if (idFieldType.equals(Long.class) || idFieldType.equals(Long.TYPE)) {
            return "{" + ID + " : " + id + "}";
        }
        //any other id type than String, Long, or ObjectId are not currently support
        throw new IllegalArgumentException("Id of type '" + id + "' is not supported");

    }

    /**
     * Get the Repository.
     * @return the repository.
     */
    @Override
    public Repository getRepository() {
        return repository;
    }
    /**
     * Not Support by Mongo
     */
    @Override
    public void executeTransactionalBlock(Runnable runnable) throws HasBeenRollBackException {
        throw new UnsupportedOperationException("MongoDB does not support transactions");

    }
    /**
     * Not Support by Mongo
     */
    @Override
    public TransactionManager getTransactionManager() {
        throw new UnsupportedOperationException("MongoDB does not support transactions");
    }

    /**
     * Not Support by Mongo
     */
    @Override
    public <R> FluentTransaction<R>.Intermediate transaction(Callable<R> callable) {
        throw new UnsupportedOperationException("MongoDB does not support transactions");
    }

    /**
     * Not Support by Mongo
     */
    @Override
    public <R> FluentTransaction<R> transaction() {
        throw new UnsupportedOperationException("MongoDB does not support transactions");
    }

    /**
     * Not Support by Mongo
     */
    @Override
    public <A> A executeTransactionalBlock(Callable<A> callable) throws HasBeenRollBackException {
        throw new UnsupportedOperationException("MongoDB does not support transactions");
    }
}
