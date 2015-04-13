package sample;


import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

public class Panda2 {


    @Id
    private String _id ="8888888";
    public int age;
    String name;

    public Panda2(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda2() {

    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}