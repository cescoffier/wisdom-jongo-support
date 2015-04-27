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

import org.bson.types.ObjectId;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.jongo.entities.PandaUsingAutoObjectId6;
import org.wisdom.jongo.entities.PandaUsingManualLong1;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test checking the Crud service.
 */
public class JongoCrudIT extends WisdomTest {

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.wisdom.jongo.entities.PandaUsingManualLong1)")
    Crud<PandaUsingManualLong1, Long> crud1;

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.wisdom.jongo.entities.PandaUsingAutoObjectId6)")
    Crud<PandaUsingAutoObjectId6, ObjectId> crud2;

    @Test
    public void testCrud1() {
        assertThat(crud1.count()).isEqualTo(0);
        PandaUsingManualLong1 panda = new PandaUsingManualLong1(25, "Paul");
        panda = crud1.save(panda);
        assertThat(panda.id()).isNotNull();
        assertThat(crud1.count()).isEqualTo(1);
        crud1.delete(panda.id());
        assertThat(crud1.count()).isEqualTo(0);
    }

    @Test
    public void testCrud2() {
        assertThat(crud2.count()).isEqualTo(0);
        PandaUsingAutoObjectId6 panda = new PandaUsingAutoObjectId6(25, "Paul");
        panda = crud2.save(panda);
        System.out.println(panda.id());
        assertThat(panda.id()).isNotNull();
        assertThat(crud2.count()).isEqualTo(1);
        crud2.delete(panda.id());
        assertThat(crud2.count()).isEqualTo(0);
    }
}
