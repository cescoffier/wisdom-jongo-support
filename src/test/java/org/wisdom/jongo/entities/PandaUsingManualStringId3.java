package org.wisdom.jongo.entities;


import org.jongo.marshall.jackson.oid.Id;

/**
 * Entity using the case 3 from http://jongo.org
 * public class Friend {
 *  @Id // manual
 *  private String key;
 * }
 */
public class PandaUsingManualStringId3 {

    @Id
    private String key ;
    public int age;
    String name;

    public PandaUsingManualStringId3(int age, String name) {
        this.age = age;
        this.name = name;
    }

    /**
     * Constructor used by Jongo.
     */
    public PandaUsingManualStringId3() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}