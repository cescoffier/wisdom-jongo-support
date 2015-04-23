package org.wisdom.jongo.bridge;

import com.google.common.collect.Iterables;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.jongo.entities.PandaUsingManualStringId3;
import org.wisdom.jongo.service.JongoCRUD;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class JongoCRUDServiceWithEntityUsingManualStringKeyTest extends JongoBaseTest {
    @Before
    public void clearDb() {
        JongoCRUDService<PandaUsingManualStringId3, ObjectId> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        jc.deleteAllFromCollection();
    }

    @Test
    public void testGetEntityClass() throws Exception {
        JongoCRUDService<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        assertThat(jc.getEntityClass()).isEqualTo(PandaUsingManualStringId3.class);
    }

    @Test
    public void testGetIdClass() throws Exception {
        JongoCRUDService<PandaUsingManualStringId3, String> jc =
                new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        assertThat(jc.getIdClass()).isEqualTo(String.class);
    }

    @Test
    public void testFindOneByID() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul");
        jc.save(p);
        PandaUsingManualStringId3 p2 = jc.findOne(p.getKey());
        assertThat(p.getKey()).isEqualTo(p2.getKey());
    }

    @Test
    public void testFindOneByIdNonExsisting() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p2 = jc.findOne("1234");
        assertThat(p2).isNull();
    }

    @Test
    public void testFindAll() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        Long count = jc.count();
        Iterable<PandaUsingManualStringId3> iterable = jc.findAll();
        assertThat(count).isEqualTo(Iterables.size(iterable));
    }

    @Test
    public void testExists() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul");
        jc.save(p);
        assertThat(jc.exists(p.getKey())).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul");
        jc.save(p);
        assertThat(jc.exists("1234")).isEqualTo(false);
    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        jc.deleteAllFromCollection();
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("123457");
        jc.save(p);
        Long count = jc.count();
        jc.delete(p.getKey());
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.getKey())).isNull();
    }

    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("123457");
        jc.save(p);
        Long count = jc.count();
        try {
            jc.delete("999999");
            fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK, the error is expected.
        }
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2);
        assertThat(jc.findOne("1234")).isNull();
    }

    @Test
    public void testDeleteIterable() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        for (int i = 0; i < 5; i++) {
            PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
            p.setKey("userpaul" + i);
            jc.save(p);
        }
        Iterable<PandaUsingManualStringId3> iterable = jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);

    }

    @Test
    public void testDeleteIterableWithNonExisting() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        List<PandaUsingManualStringId3> list = new ArrayList<>();
        Long count = jc.count();
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul1");

        PandaUsingManualStringId3 p2 = new PandaUsingManualStringId3(23, "Pau2l");
        p2.setKey("userpaul2");

        PandaUsingManualStringId3 p3 = new PandaUsingManualStringId3(23, "Paul3");
        p3.setKey("userpaul3");

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
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul");
        jc.save(p);
        Long count = jc.count();
        jc.delete(p);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.getKey())).isNull();
    }

    @Test
    public void testDeleteEntityNonExisting() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul");
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
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul");
        jc.save(p);
        assertThat(p.getKey()).isNotNull().isNotEmpty();
    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul");
        jc.save(p);
        p.age = 24;
        PandaUsingManualStringId3 p2 = jc.save(p);
        assertThat(p.getKey()).isEqualTo(p2.getKey());
    }

    @Test
    public void testSaveIterable() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        List<PandaUsingManualStringId3> list = new ArrayList<>();
        PandaUsingManualStringId3 p = new PandaUsingManualStringId3(23, "Paul");
        p.setKey("userpaul1");

        PandaUsingManualStringId3 p2 = new PandaUsingManualStringId3(23, "Pau2l");
        p2.setKey("userpaul2");
        jc.save(p2);

        PandaUsingManualStringId3 p3 = new PandaUsingManualStringId3(23, "Paul3");
        p3.setKey("userpaul3");
        jc.save(p3);
        p3.age = 45;
        list.add(p);
        list.add(p2);
        list.add(p3);
        Iterable<PandaUsingManualStringId3> it2 = jc.save(list);
        assertThat(list).containsExactlyElementsOf(it2);
    }

    @Test
    public void testCountItemsInCollection() throws Exception {
        JongoCRUD<PandaUsingManualStringId3, String> jc = new JongoCRUDService<>(PandaUsingManualStringId3.class, db());
        jc.deleteAllFromCollection();
        long count = jc.count();
        PandaUsingManualStringId3 p1 = new PandaUsingManualStringId3(23, "Paul");
        p1.setKey("12345");
        jc.save(p1);
        PandaUsingManualStringId3 p2 = new PandaUsingManualStringId3(23, "Paul");
        p2.setKey("12346");
        jc.save(p2);
        long newCount = jc.count();
        assertThat(newCount).isEqualTo(count + 2);
    }
}

