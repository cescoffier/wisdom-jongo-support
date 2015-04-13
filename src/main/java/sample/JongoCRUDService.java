//TODO it seems to me you can only delete odjectids in jongo, even if jongo lets you manualy create ids of a differnt type

package sample;

import com.mongodb.DB;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.wisdom.api.model.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.jongo.Oid.withOid;


public class JongoCRUDService<T, K extends Serializable> implements JongoCRUD<T, K> {

    private final DB db;
    private final Class<T> entityClass;
    private final Class<K> entityKeyClass;
    private final MongoCollection collection;
    private final Jongo jongo;
    private final Field idField;
    private Class idFieldType;

    /**
     * Constructor
     * @param clazz the class using the service.
     * @param db the database the service is connecting to.
     */
    public JongoCRUDService(Class<T> clazz, DB db) {
        this.db = db;
        this.entityClass = clazz;
        //Todo should call get data store?
        jongo = new Jongo(db);
        collection = jongo.getCollection(entityClass.getSimpleName());
        this.idField = findIdField();
        entityKeyClass = (Class<K>) this.idField.getType();

    }

    public Class getIdFieldType() {
        return idFieldType;
    }

    public void setIdFieldType(Class idFieldType) {
        this.idFieldType = idFieldType;
    }

    /**
     * Get the value inside of the id field of the entity.
     *
     * @param o the object whos id we want to retrieve.
     * @return
     */
    private Object getIdFieldValue(T o) {
        if (!idField.isAccessible()) {
            idField.setAccessible(true);
        }
        return getEntityId(o);
    }

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
     * Jongo seems to be limited to ids that are named _id of type String annotated with @ObjectId or
     * any name of type string annotated with both @ObjectId and @Id. Otherwise there are problems when you later try
     * to remove the object.
     *
     * @return returns the field that has the correct annotations.
     */
    private Field findIdField() {
        if (idField != null) {
            return idField;
        }

        //check all declared fields first
        for (Field field : entityClass.getDeclaredFields()) {
            if (checkAnnotations(field) != null) {
                return field;
            }
        }
        //If not found above check in the parent classes
        for (Field field : entityClass.getFields()) {
            if (checkAnnotations(field) != null) {
                return field;
            }
        }

        throw new IllegalStateException("Cannot find the id field inside " + entityClass.getName());
    }

    /**
     * Check each field to see if it has the annotations we are looking for.
     *
     * @param field from an entity.
     * @return the field if it has the correct annotations otherwise returns null. Assumes that there isn't more than
     * one field with correct annotations.
     */
    private Field checkAnnotations(Field field) {
        Class type = field.getType();
        String name = field.getName();

        org.jongo.marshall.jackson.oid.ObjectId objectId = field.getAnnotation(org.jongo.marshall.jackson.oid.ObjectId.class);
        org.jongo.marshall.jackson.oid.Id id = field.getAnnotation(org.jongo.marshall.jackson.oid.Id.class);

        if (name.equals("_id") && objectId != null
                || id != null && objectId != null) {
            setIdFieldType(ObjectId.class);
            return field;
        }

        if (id != null || type.equals(ObjectId.class)) {  // objectId is null
            setIdFieldType(type);
            return field;
        }

        return null;
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

    @Override
    public Jongo getJongoDataStore() {
        return new Jongo(db);
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
        WriteResult result = collection.save(o);
        if (result.getError() != null) {
            throw new RuntimeException("Cannot save instance " + o + " in " + collection.getName() + " : " + result.getLastError());
        } else {
            return o;
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
        List<T> list = new ArrayList<T>();
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



    //todo not tested
    @Override
    public T findOne(EntityFilter<T> filter) {
        //TODO Support jongo filter
        for (T entity : findAll()) {
            if (filter.accept(entity)) {
                return entity;
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

    @Override
    public Iterable<T> findAll(EntityFilter<T> entityFilter) {
        List<T> entities = new ArrayList<>();
        for (T entity : findAll()) {
            if (entityFilter.accept(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    /**
     *
     * Delete an object by  from the collection if it exists.
     *
     * @param id of the object you wish to delete for.
     *           If the id doesn't exist there is an IllegalArgumentException.
     *           <p>
     *           Note: as far as I can tell jongo only supports remove for object id types and not others.
     */
    @Override
    public void delete(K id) {
        if (idFieldType.equals(ObjectId.class)) {
            collection.remove(withOid(String.valueOf(id)));
        } else {
            WriteResult result = collection.remove(createIdQuery(id));
           //get n is number of docs effected by operation in mongo
            if (result.getN()==0){
                throw new IllegalArgumentException("Unable to delete Id '" + id + "' not found");
            }
        }
    }

    /**
     *
     * Delete an object by  from the collection if it exists.
     *
     * @param o is an object that is an entity. It needs to have a valid _id field.
     * @return returns the original object passed in.
     * If the id doesn't exist there is an IllegalArgumentException.
     */
    @Override
    public T delete(T o) {
        K id = getEntityId(o);
      /*  if (id == null) {
            throw new IllegalArgumentException(" Cannot extract id from " + o);
        }*/
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
     *
     * Checks to see if the object exsists in the Mongo Collection based on its ID.
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
        MongoCursor<T> cursor = collection.find().as(entityClass);
        return cursor.count();
    }

    private String createIdQuery(K id) {
        if (idFieldType.equals(ObjectId.class)) {
            return withOid(id.toString());
        }

        if (idFieldType.equals(String.class)) {
            return "{_id : '" + id + "'}";
        }

        if (idFieldType.equals(Long.class) || idFieldType.equals(Long.TYPE)) {
            return "{_id : "+id+"}";
        }

        throw new IllegalArgumentException("Id of type '" + id + "' is not supported");

    }

    //todo
    @Override
    public Repository getRepository() {
        return null;
    }

    /*--------------------------------------------------------------------------------------------*/
    @Override
    public void executeTransactionalBlock(Runnable runnable) throws HasBeenRollBackException {
        throw new UnsupportedOperationException("MongoDB does not support transactions");

    }

    @Override
    public TransactionManager getTransactionManager() {
        throw new UnsupportedOperationException("MongoDB does not support transactions");
    }

    @Override
    //used to be R
    public FluentTransaction<T>.Intermediate transaction(Callable callable) {
        throw new UnsupportedOperationException("MongoDB does not support transactions");
    }

    @Override
    //used to be R
    public FluentTransaction<T> transaction() {
        throw new UnsupportedOperationException("MongoDB does not support transactions");
    }

    @Override
    //used to be A
    public T executeTransactionalBlock(Callable callable) throws HasBeenRollBackException {
        throw new UnsupportedOperationException("MongoDB does not support transactions");
    }
}
