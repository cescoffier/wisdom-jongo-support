package org.wisdom.jongo.sample;


import org.jongo.marshall.jackson.oid.Id;

public class Panda3_ID_String {


    @Id
    private String _id ;
    public int age;
    String name;

    public Panda3_ID_String(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda3_ID_String() {

    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}