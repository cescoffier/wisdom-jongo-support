package org.wisdom.jongo.sample;


import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

public class Panda5_ObjID_ID_StringKey {

    @ObjectId // auto
    @Id
    private String key ;
    public int age;
    String name;

    public Panda5_ObjID_ID_StringKey(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda5_ObjID_ID_StringKey() {

    }

    public String get_id() {
        return key;
    }

    public void set_id(String _id) {
        this.key = key;
    }
}