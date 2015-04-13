package sample;


import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

public class Panda {

    @ObjectId // auto
    @Id
    private String _id ;
    public int age;
    String name;

    public Panda(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda() {

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}