package org.wisdom.jongo.service;

import org.wisdom.api.model.EntityFilter;

/**
 * An {@link EntityFilter} implementation to use MongoDB query.
 */
public class MongoFilter<T> implements EntityFilter<T> {

    private final String filter;
    private final Object[] params;

    public MongoFilter(final String filter) {
        this.filter = filter;
        this.params = new Object[0];
    }

    public MongoFilter(final String filter, final Object... params) {
        this.filter = filter;
        this.params = params;
    }

    @Override
    public boolean accept(T t) {
        // Do nothing on purpose - the filter is applied directly on the DB layer.
        return false;
    }

    public String getFilter() {
        return filter;
    }

    public Object[] getParams() {
        return params;
    }
}
