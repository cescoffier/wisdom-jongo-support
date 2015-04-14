package org.wisdom.jongo.sample;


public class Panda1_idlong {



    private Long _id;
    public int age;
    String name;

    public Panda1_idlong(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda1_idlong() {

    }
    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }


}