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
import org.junit.Before;
import org.junit.Test;
import org.wisdom.jongo.entities.PandaUsingManualLongId2;
import org.wisdom.jongo.service.JongoCRUD;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class JongoCRUDServiceWithEntityUsingManualLongKeyTest extends JongoBaseTest {
    @Before
    public void clearDb() {
        JongoCRUDService<PandaUsingManualLongId2, Long> jc =
                new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        jc.deleteAllFromCollection();
    }

    @Test
    public void testGetEntityClass() throws Exception {
        JongoCRUDService<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        assertThat(jc.getEntityClass()).isEqualTo(PandaUsingManualLongId2.class);
    }

    @Test
    public void testGetIdClass() throws Exception {
        JongoCRUDService<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        assertThat(jc.getIdClass()).isEqualTo(Long.TYPE);
    }

    @Test
    public void testFindOneByID() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(88L);
        jc.save(p);
        PandaUsingManualLongId2 p2 = jc.findOne(p.getKey());
        assertThat(p.getKey()).isEqualTo(p2.getKey());
    }

    @Test
    public void testFindOneByIdNonExsisting() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p2 = jc.findOne(1234L);
        assertThat(p2).isNull();
    }

    @Test
    public void testFindAll() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        Long count = jc.count();
        Iterable<PandaUsingManualLongId2> iterable = jc.findAll();
        assertThat(count).isEqualTo(Iterables.size(iterable));
    }

    @Test
    public void testExists() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);
        jc.save(p);
        assertThat(jc.exists(p.getKey())).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);
        jc.save(p);
        assertThat(jc.exists(1234L)).isEqualTo(false);
    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);
        jc.save(p);
        Long count = jc.count();
        jc.delete(p.getKey());
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.getKey())).isNull();
    }


    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);
        jc.save(p);
        Long count = jc.count();
        try {
            jc.delete(1234L);
            fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK, the error is expected.
        }
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2);
        assertThat(jc.findOne(1234L)).isNull();
    }


    @Test
    public void testDeleteIterable() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());

        for (int i = 0; i < 5; i++) {
            PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
            p.setKey(189L + i);
            jc.save(p);
        }
        Iterable<PandaUsingManualLongId2> iterable = jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);
    }


    @Test
    public void testDeleteIterableWithNonExsisting() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        List<PandaUsingManualLongId2> list = new ArrayList<PandaUsingManualLongId2>();
        Long count = jc.count();
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);

        PandaUsingManualLongId2 p2 = new PandaUsingManualLongId2(23, "Paul2");
        p2.setKey(187L);

        PandaUsingManualLongId2 p3 = new PandaUsingManualLongId2(23, "Paul3");
        p3.setKey(186L);

        list.add(jc.save(p));
        list.add(p2);
        list.add(jc.save(p3));

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
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);
        jc.save(p);
        Long count = jc.count();
        jc.delete(p);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.getKey())).isNull();
    }


    @Test
    public void testDeleteEntityNonExisting() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);
        Long count = jc.count();
        try {
            jc.delete(p);
            fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK, the error is expected.
        }
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2);
        assertThat(jc.findOne(p.getKey())).isNull();
    }

    @Test
    public void testSaveNewEntity() throws Exception {

        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);
        jc.save(p);
        assertThat(p.getKey()).isNotNull();

    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(189L);
        jc.save(p);
        p.age = 24;
        PandaUsingManualLongId2 p2 = jc.save(p);
        assertThat(p.getKey()).isEqualTo(p2.getKey());

    }

    @Test
    public void testSaveIterable() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        List<PandaUsingManualLongId2> list = new ArrayList<>();
        PandaUsingManualLongId2 p = new PandaUsingManualLongId2(23, "Paul");
        p.setKey(187L);
        jc.save(p);
        PandaUsingManualLongId2 p2 = new PandaUsingManualLongId2(23, "Paul2");
        p2.setKey(189L);
        jc.save(p2);
        PandaUsingManualLongId2 p3 = new PandaUsingManualLongId2(23, "Paul3");
        p3.setKey(188L);
        jc.save(p3);
        p3.age = 45;

        list.add(p);
        list.add(p2);
        list.add(p3);
        Iterable<PandaUsingManualLongId2> it2 = jc.save(list);
        assertThat(list).containsExactlyElementsOf(it2);
    }

    @Test
    public void testCountItemsInCollection() throws Exception {
        JongoCRUD<PandaUsingManualLongId2, Long> jc = new JongoCRUDService<>(PandaUsingManualLongId2.class, db());
        jc.deleteAllFromCollection();
        long count = jc.count();
        PandaUsingManualLongId2 p1 = new PandaUsingManualLongId2(23, "Paul");
        p1.setKey(89L);
        jc.save(p1);
        PandaUsingManualLongId2 p2 = new PandaUsingManualLongId2(23, "Paul");
        p2.setKey(90L);
        jc.save(p2);
        long newCount = jc.count();
        assertThat(newCount).isEqualTo(count + 2);
    }
}

