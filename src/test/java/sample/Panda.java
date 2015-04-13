package sample;


import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

public class Panda {

    @ObjectId // auto
    @Id
    public String _id ;
    public int age;
    String name;

    public Panda(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda() {

    }
}