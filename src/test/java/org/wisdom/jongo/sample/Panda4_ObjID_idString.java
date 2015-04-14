package org.wisdom.jongo.sample;


import org.jongo.marshall.jackson.oid.ObjectId;

public class Panda4_ObjID_idString {

    @ObjectId // auto
    private String _id ;
    public int age;
    String name;

    public Panda4_ObjID_idString(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda4_ObjID_idString() {

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}