package org.wisdom.jongo.entities;


/**
 * Entity using the case 1 from http://jongo.org
 * public class Friend {
 * // manual
 * private long _id;
 * }
 */
public class PandaUsingManualLong1 {

    // manual
    private long _id;

    public int age;
    String name;

    public PandaUsingManualLong1(int age, String name) {
        this.age = age;
        this.name = name;
    }

    /**
     * Constructor used by jongo.
     */
    public PandaUsingManualLong1() {

    }

    public Long id() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }


}