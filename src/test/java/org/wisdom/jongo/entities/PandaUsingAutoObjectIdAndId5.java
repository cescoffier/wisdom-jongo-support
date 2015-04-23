package org.wisdom.jongo.entities;


import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Entity using the case 5 from http://jongo.org
 * public class Friend {
 *  @Id @ObjectId // auto
 *  private String key;
 * }
 */
public class PandaUsingAutoObjectIdAndId5 {

    @ObjectId // auto
    @Id
    private String key ;
    public int age;
    String name;

    public PandaUsingAutoObjectIdAndId5(int age, String name) {
        this.age = age;
        this.name = name;
    }

    /**
     * Constructor used by Jongo.
     */
    public PandaUsingAutoObjectIdAndId5() {

    }

    public String getKey() {
        return key;
    }
}