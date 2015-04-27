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
package org.wisdom.jongo.bridge;

import com.google.common.collect.Iterables;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.jongo.entities.PandaUsingAutoObjectId6;
import org.wisdom.jongo.service.JongoCRUD;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 * Check the Jongo Crud implementation with the case 6.
 */
public class JongoCRUDServiceWithEntityUsingAutoObjectIdTest extends JongoBaseTest {

    @Before
    public void clearDb() {
        JongoCRUDService<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        jc.deleteAllFromCollection();
    }

    @Test
    public void testGetEntityClass() throws Exception {
        JongoCRUDService<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        assertThat(jc.getEntityClass()).isEqualTo(PandaUsingAutoObjectId6.class);
    }

    @Test
    public void testGetIdClass() throws Exception {
        JongoCRUDService<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        assertThat(jc.getIdClass()).isEqualTo(ObjectId.class);
    }

    @Test
    public void testFindOneByID() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class,
                db());
        PandaUsingAutoObjectId6 p = jc.save(new PandaUsingAutoObjectId6(23, "Paul"));
        PandaUsingAutoObjectId6 p2 = jc.findOne(p.id());
        assertThat(p.id()).isEqualTo(p2.id());
    }

    @Test
    public void testFindOneByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class,
                db());
        ObjectId id = new ObjectId("507f191e810c19729de860ea");
        PandaUsingAutoObjectId6 p2 = jc.findOne(id);
        assertThat(p2).isNull();
    }

    @Test
    public void testFindAll() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        Long count = jc.count();
        Iterable<PandaUsingAutoObjectId6> iterable = jc.findAll();
        assertThat(count).isEqualTo(Iterables.size(iterable));
    }

    @Test
    public void testExists() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        PandaUsingAutoObjectId6 p = jc.save(new PandaUsingAutoObjectId6(23, "Paul"));
        assertThat(jc.exists(p.id())).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        ObjectId id = new ObjectId("507f191e810c19729de860ea");
        assertThat(jc.exists(id)).isEqualTo(false);

    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        PandaUsingAutoObjectId6 p = jc.save(new PandaUsingAutoObjectId6(23, "Paul"));
        Long count = jc.count();
        jc.delete(p.id());
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.id())).isNull();
    }


    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        ObjectId id = new ObjectId("507f191e810c19729de860ea");
        jc.save(new PandaUsingAutoObjectId6(23, "Paul"));
        Long count = jc.count();
        try {
            jc.delete(id);
            fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK, the error is expected.
        }
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2);
        assertThat(jc.findOne(id)).isNull();
    }

    @Test
    public void testDeleteIterable() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());

        for (int i = 0; i < 5; i++) {
            jc.save(new PandaUsingAutoObjectId6(i, "Paul"));
        }
        Iterable<PandaUsingAutoObjectId6> iterable = jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);
    }

    @Test
    public void testDeleteIterableWithNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        List<PandaUsingAutoObjectId6> list = new ArrayList<>();

        // Initial count.
        Long count = jc.count();

        // Populate
        final PandaUsingAutoObjectId6 panda1 = jc.save(new PandaUsingAutoObjectId6(1, "Paul"));
        final PandaUsingAutoObjectId6 panda2 = jc.save(new PandaUsingAutoObjectId6(5, "Paul"));
        final PandaUsingAutoObjectId6 panda_not_in_db = new PandaUsingAutoObjectId6(2, "Paul");

        list.add(panda1);
        list.add(panda_not_in_db);
        list.add(panda2);
        try {
            jc.delete(list);
            fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK, the error is expected.
        }

        assertThat(jc.count()).isGreaterThan(count);
    }

    @Test
    public void testDeleteEntity() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        PandaUsingAutoObjectId6 p = jc.save(new PandaUsingAutoObjectId6(23, "Paul"));
        Long count = jc.count();
        jc.delete(p);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.id())).isNull();
    }

    @Test
    public void testDeleteEntityNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        PandaUsingAutoObjectId6 p = new PandaUsingAutoObjectId6(23, "Paul");
        Long count = jc.count();
        try {
            jc.delete(p);
            fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK, the error is expected.
        }
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2);
        assertThat(jc.findOne(p.id())).isNull();
    }

    @Test
    public void testSaveNewEntity() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        PandaUsingAutoObjectId6 p = jc.save(new PandaUsingAutoObjectId6(25, "Jeff"));
        assertThat(p.id()).isNotNull();
    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        PandaUsingAutoObjectId6 p = jc.save(new PandaUsingAutoObjectId6(23, "Paul"));
        p.age = 24;
        PandaUsingAutoObjectId6 p2 = jc.save(p);
        assertThat(p.id()).isEqualTo(p2.id());
    }

    @Test
    public void testSaveIterable() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        List<PandaUsingAutoObjectId6> list = new ArrayList<>();
        PandaUsingAutoObjectId6 p = (new PandaUsingAutoObjectId6(23, "Paul"));
        PandaUsingAutoObjectId6 p2 = jc.save(new PandaUsingAutoObjectId6(13, "Paula"));
        PandaUsingAutoObjectId6 p3 = jc.save(new PandaUsingAutoObjectId6(21, "Pam"));
        p3.age = 45;
        list.add(p);
        list.add(p2);
        list.add(p3);
        Iterable<PandaUsingAutoObjectId6> it2 = jc.save(list);
        assertThat(list).containsExactlyElementsOf(it2);
    }

    @Test
    public void testCountItemsInCollection() throws Exception {
        JongoCRUD<PandaUsingAutoObjectId6, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoObjectId6.class, db());
        long count = jc.count();
        jc.save(new PandaUsingAutoObjectId6(23, "Paul"));
        jc.save(new PandaUsingAutoObjectId6(23, "pam"));
        long newCount = jc.count();
        assertThat(newCount).isEqualTo(count + 2);
    }
}

