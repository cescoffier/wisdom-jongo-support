package org.wisdom.jongo.entities;


import org.jongo.marshall.jackson.oid.Id;

/**
 * Entity using the case 2 from http://jongo.org
 * public class Friend {
 *  @Id // manual
 *  private long key;
 * }
 */
public class PandaUsingManualLongId2 {

    @Id
    private long key;

    public int age;
    String name;

    public PandaUsingManualLongId2(int age, String name) {
        this.age = age;
        this.name = name;
    }

    /**
     * Constructor used by Jongo.
     */
    public PandaUsingManualLongId2() {

    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }
}