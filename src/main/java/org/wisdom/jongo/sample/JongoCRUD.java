package org.wisdom.jongo.sample;

import org.jongo.Jongo;
import org.wisdom.api.model.Crud;

import java.io.Serializable;

public interface JongoCRUD<T,K extends Serializable> extends Crud<T,K> {
    Jongo getJongoDataStore();

    void deleteAllFromCollection();


}
