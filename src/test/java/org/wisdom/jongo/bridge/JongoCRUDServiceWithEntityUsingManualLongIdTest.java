package org.wisdom.jongo.bridge;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.jongo.entities.PandaUsingManualLong1;
import org.wisdom.jongo.service.JongoCRUD;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class JongoCRUDServiceWithEntityUsingManualLongIdTest extends JongoBaseTest {

    @Before
    public void clearDb() {
        JongoCRUDService<PandaUsingManualLong1, Long> jc =
                new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        jc.deleteAllFromCollection();
    }

    @Test
    public void testGetEntityClass() throws Exception {
        JongoCRUDService<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        assertThat(jc.getEntityClass()).isEqualTo(PandaUsingManualLong1.class);
    }

    @Test
    public void testGetIdClass() throws Exception {
        JongoCRUDService<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        assertThat(jc.getIdClass()).isEqualTo(Long.TYPE);
    }

    @Test
    public void testFindOneByID() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(88L);
        jc.save(p);
        PandaUsingManualLong1 p2 = jc.findOne(p.id());
        assertThat(p.id()).isEqualTo(p2.id());
    }

    @Test
    public void testFindOneByIdNonExsisting() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p2 = jc.findOne(1234L);
        assertThat(p2).isNull();
    }

    @Test
    public void testFindAll() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        Long count = jc.count();
        Iterable<PandaUsingManualLong1> iterable = jc.findAll();
        assertThat(count).isEqualTo(Iterables.size(iterable));
    }

    @Test
    public void testExists() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);
        jc.save(p);
        assertThat(jc.exists(p.id())).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {

        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);
        jc.save(p);
        assertThat(jc.exists(1234L)).isEqualTo(false);

    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(189L);
        jc.save(p);
        Long count = jc.count();
        jc.delete(p.id());
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.id())).isNull();
    }


    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);
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
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());

        for (int i = 0; i < 5; i++) {
            PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
            p.setId(400L + i);
            jc.save(p);
        }
        Iterable<PandaUsingManualLong1> iterable = jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);

    }

    @Test
    public void testDeleteIterableWithNonExsisting() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        List<PandaUsingManualLong1> list = new ArrayList<>();
        Long count = jc.count();

        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);

        list.add(jc.save(p));

        PandaUsingManualLong1 p1 = new PandaUsingManualLong1(23, "Paul");
        p.setId(405L);
        list.add(p1);
        PandaUsingManualLong1 p2 = new PandaUsingManualLong1(23, "Paul");
        p.setId(402L);

        list.add(jc.save(p2));
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
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);
        jc.save(p);
        Long count = jc.count();
        jc.delete(p);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.id())).isNull();
    }


    @Test
    public void testDeleteEntityNonExisting() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);
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
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);
        jc.save(p);
        assertThat(p.id()).isNotNull();
    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);
        jc.save(p);
        p.age = 24;
        PandaUsingManualLong1 p2 = jc.save(p);
        assertThat(p.id()).isEqualTo(p2.id());
    }

    @Test
    public void testSaveIterable() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        List<PandaUsingManualLong1> list = new ArrayList<>();
        PandaUsingManualLong1 p = new PandaUsingManualLong1(23, "Paul");
        p.setId(400L);
        jc.save(p);
        PandaUsingManualLong1 p2 = new PandaUsingManualLong1(23, "Paul2");
        p2.setId(402L);
        jc.save(p2);
        PandaUsingManualLong1 p3 = new PandaUsingManualLong1(23, "Paul3");
        p3.setId(403L);
        jc.save(p3);
        p3.age = 45;
        list.add(p);
        list.add(p2);
        list.add(p3);
        Iterable<PandaUsingManualLong1> it2 = jc.save(list);
        assertThat(list).containsExactlyElementsOf(it2);


    }

    @Test
    public void testCountItemsInCollection() throws Exception {
        JongoCRUD<PandaUsingManualLong1, Long> jc = new JongoCRUDService<>(PandaUsingManualLong1.class, db());
        jc.deleteAllFromCollection();
        long count = jc.count();
        PandaUsingManualLong1 p1 = new PandaUsingManualLong1(23, "Paul");
        p1.setId(89L);
        jc.save(p1);
        PandaUsingManualLong1 p2 = new PandaUsingManualLong1(23, "Paul");
        p2.setId(90L);
        jc.save(p2);
        long newCount = jc.count();
        assertThat(newCount).isEqualTo(count + 2);

    }
}

