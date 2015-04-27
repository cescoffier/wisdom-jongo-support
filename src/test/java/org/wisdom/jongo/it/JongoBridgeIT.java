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
package org.wisdom.jongo.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ow2.chameleon.testing.helpers.OSGiHelper;
import org.wisdom.api.model.Crud;
import org.wisdom.api.model.Repository;
import org.wisdom.test.parents.WisdomTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test checking the crud instantiation.
 */
public class JongoBridgeIT extends WisdomTest {

    private OSGiHelper osgi;

    @Before
    public void setUp() {
        osgi = new OSGiHelper(context);
    }

    @After
    public void after() {
        osgi.dispose();
    }

    @Test
    public void testServices() {
        final Repository repository = osgi.getServiceObject(Repository.class);
        assertThat(repository).isNotNull();
        assertThat(repository.getName()).isEqualToIgnoringCase("kitten");
        assertThat(repository.getCrudServices()).hasSize(2);
        final List<Crud> cruds = osgi.getServiceObjects(Crud.class);
        assertThat(cruds).hasSize(2);
    }


}
