package sample;

import org.jongo.Jongo;
import org.wisdom.api.model.Crud;

public interface JongoCRUD<T> extends Crud<T,String> {
    Jongo getJongoDataStore();
}
