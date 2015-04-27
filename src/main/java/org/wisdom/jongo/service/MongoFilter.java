/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2015 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
