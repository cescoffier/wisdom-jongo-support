package sample;


import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

public class Panda4 {


    @Id
    private Long _id =88l;
    public int age;
    String name;

    public Panda4(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Panda4() {

    }
    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }


}