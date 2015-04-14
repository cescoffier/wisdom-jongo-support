package org.wisdom.jongo.sample;


public class Panda6_objidId {


    private org.bson.types.ObjectId _id ;
    public int age;
    String name;

    public Panda6_objidId(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda6_objidId() {

    }

    public org.bson.types.ObjectId get_id() {
        return _id;
    }

    public void set_id(org.bson.types.ObjectId _id) {
        this._id = _id;
    }
}