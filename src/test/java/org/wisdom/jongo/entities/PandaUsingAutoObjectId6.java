package org.wisdom.jongo.entities;


import org.bson.types.ObjectId;

/**
 * Entity using the case 6 from http://jongo.org
 * public class Friend {
 * // auto
 * private ObjectId _id;
 * }
 */
public class PandaUsingAutoObjectId6 {

    // auto
    private ObjectId _id;

    public int age;
    String name;

    public PandaUsingAutoObjectId6(int age, String name) {
        this.age = age;
        this.name = name;
    }

    /**
     * Constructor used by jongo.
     */
    public PandaUsingAutoObjectId6() {

    }

    public ObjectId id() {
        return _id;
    }
}