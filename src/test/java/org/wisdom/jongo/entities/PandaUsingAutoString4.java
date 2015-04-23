package org.wisdom.jongo.entities;


import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Entity using the case 4 from http://jongo.org
 * public class Friend {
 * @ObjectId // auto
 * private String _id;
 * }
 */
public class PandaUsingAutoString4 {

    @ObjectId // auto
    private String _id;

    public int age;
    String name;

    public PandaUsingAutoString4(int age, String name) {
        this.age = age;
        this.name = name;
    }

    /**
     * Constructor used by Jongo.
     */
    public PandaUsingAutoString4() {

    }

    public String id() {
        return _id;
    }

    public String getName() {
        return name;
    }
}