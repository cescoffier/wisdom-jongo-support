package sample;

import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Created by jennifer on 3/20/15.
 */
public class Friend {

    @ObjectId
    String _id;



    int age;
    String name="";

    public Friend(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Friend() {

    }
}
