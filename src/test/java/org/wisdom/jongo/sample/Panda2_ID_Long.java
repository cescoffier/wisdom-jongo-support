package org.wisdom.jongo.sample;


import org.jongo.marshall.jackson.oid.Id;

public class Panda2_ID_Long {


    @Id
    private Long _id;
    public int age;
    String name;

    public Panda2_ID_Long(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda2_ID_Long() {

    }
    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }


}