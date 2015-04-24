package org.wisdom.jongo.service;

import org.wisdom.api.model.Crud;

import java.io.Serializable;

public interface JongoCRUD<T,K extends Serializable> extends Crud<T,K> {

    void deleteAllFromCollection();


}
