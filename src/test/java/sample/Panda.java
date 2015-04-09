package sample;

import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Created by jennifer on 4/3/15.
 */
public class Panda {

    @ObjectId // auto generate id
            String _id;

    int age;
    String name;

    public Panda(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda() {

    }
}