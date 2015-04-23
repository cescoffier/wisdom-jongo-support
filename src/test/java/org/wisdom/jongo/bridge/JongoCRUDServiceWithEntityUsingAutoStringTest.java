package org.wisdom.jongo.bridge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.api.model.EntityFilter;
import org.wisdom.jongo.entities.PandaUsingAutoString4;
import org.wisdom.jongo.service.JongoCRUD;
import org.wisdom.jongo.service.MongoFilter;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class JongoCRUDServiceWithEntityUsingAutoStringTest extends JongoBaseTest {

    @Before
    public void clearDb() {
        JongoCRUDService<PandaUsingAutoString4, ObjectId> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        jc.deleteAllFromCollection();
    }

    @Test
    public void testGetEntityClass() throws Exception {
        JongoCRUDService<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        assertThat(jc.getEntityClass()).isEqualTo(PandaUsingAutoString4.class);
    }

    @Test
    public void testGetIdClass() throws Exception {
        JongoCRUDService<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        assertThat(jc.getIdClass()).isEqualTo(String.class);
    }

    @Test
    public void testFindOneByID() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = jc.save(new PandaUsingAutoString4(23, "Paul"));
        PandaUsingAutoString4 p2 = jc.findOne(p.id());
        assertThat(p.id()).isEqualTo(p2.id());
    }

    @Test
    public void testFindByEntityFilter() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = jc.save(new PandaUsingAutoString4(23, "Paul"));
        PandaUsingAutoString4 p2 = jc.findOne(new EntityFilter<PandaUsingAutoString4>() {
            @Override
            public boolean accept(PandaUsingAutoString4 panda) {
                return panda.age == 23;
            }
        });
        assertThat(p.id()).isEqualTo(p2.id());

        PandaUsingAutoString4 p3 = jc.findOne(new EntityFilter<PandaUsingAutoString4>() {
            @Override
            public boolean accept(PandaUsingAutoString4 panda) {
                return panda.age == -1;
            }
        });
        assertThat(p3).isNull();
    }

    @Test
    public void testFindByMongoFilter() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = jc.save(new PandaUsingAutoString4(23, "Paul"));
        PandaUsingAutoString4 p2 = jc.findOne(
                new MongoFilter<PandaUsingAutoString4>("{name: 'Paul'}"));
        assertThat(p.id()).isEqualTo(p2.id());

        p2 = jc.findOne(
                new MongoFilter<PandaUsingAutoString4>("{name: #}", "Paul"));
        assertThat(p.id()).isEqualTo(p2.id());

        PandaUsingAutoString4 p3 = jc.findOne(
                new MongoFilter<PandaUsingAutoString4>("{name: 'Wisdom'}")
        );
        assertThat(p3).isNull();
    }


    @Test
    public void testFindOneByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoString4,String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p2 = jc.findOne("1234");
        assertThat(p2).isNull();
    }

    @Test
    public void testFindAll() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc =
                new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        long count =jc.count();
        Iterable<PandaUsingAutoString4> iterable = jc.findAll();
        assertThat(count).isEqualTo(Iterables.size(iterable));

        List<PandaUsingAutoString4> pandas = new ArrayList<>();
        // Create a couple of Pandas
        for (int i= 0; i<5;i++){
            final PandaUsingAutoString4 panda =
                    new PandaUsingAutoString4(i, "Paul-" + i);
            jc.save(panda);
            pandas.add(panda);
        }

        assertThat(Iterables.size(jc.findAll())).isEqualTo((int) count + 5);

        // Pick the first and last pandas
        List<String> ids = ImmutableList.of(
                Iterables.get(pandas, 0).id(),
                Iterables.getLast(pandas).id()
        );

        Iterable<PandaUsingAutoString4> selected = jc.findAll(ids);
        assertThat(Iterables.size(selected)).isEqualTo(2);

        // Pick a missing panda
        ids = ImmutableList.of(
                Iterables.get(pandas, 0).id(),
                Iterables.getLast(pandas).id(),
                "missing_panda"
        );

        try {
            selected = jc.findAll(ids);
            assertThat(Iterables.size(selected)).isEqualTo(2);
            Assertions.fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK
        }

        selected = jc.findAll(new EntityFilter<PandaUsingAutoString4>() {
            @Override
            public boolean accept(PandaUsingAutoString4 panda) {
                return panda.age <= 1;
            }
        });
        assertThat(Iterables.size(selected)).isEqualTo(2);

        selected = jc.findAll(new EntityFilter<PandaUsingAutoString4>() {
            @Override
            public boolean accept(PandaUsingAutoString4 panda) {
                return panda.age < -1;
            }
        });
        assertThat(Iterables.size(selected)).isEqualTo(0);

        selected = jc.findAll(new MongoFilter<PandaUsingAutoString4>(
                "{name:#, age:#}", "Paul-1", 1));
        assertThat(Iterables.size(selected)).isEqualTo(1);
    }

    @Test
    public void testExists() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = jc.save(new PandaUsingAutoString4(23, "Paul"));
        assertThat(jc.exists(p.id())).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        jc.save(new PandaUsingAutoString4(23, "Paul"));
        assertThat(jc.exists("1234")).isEqualTo(false);

    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = jc.save(new PandaUsingAutoString4(23, "Paul"));
        Long count = jc.count();
        jc.delete(p.id());
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2+1);
        assertThat(jc.findOne(p.id())).isNull();
    }


    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        jc.save(new PandaUsingAutoString4(23, "Paul"));
        Long count = jc.count();
        try {
            jc.delete("1234");
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
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());

        for (int i= 0; i<5;i++){
            jc.save(new PandaUsingAutoString4(i, "Paul"));
        }
        Iterable<PandaUsingAutoString4> iterable =jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);

    }

    @Test
    public void testDeleteIterableWithNonExsisting() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        List<PandaUsingAutoString4> list = new ArrayList<>();
        Long count = jc.count();
        list.add(jc.save(new PandaUsingAutoString4(1, "Paul")));
        list.add(new PandaUsingAutoString4(5, "Paul"));
        list.add(jc.save(new PandaUsingAutoString4(2, "Paul")));
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
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = jc.save(new PandaUsingAutoString4(23, "Paul"));
        Long count = jc.count();
        jc.delete(p);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2+1);
        assertThat(jc.findOne(p.id())).isNull();
    }

    @Test
    public void testDeleteEntityNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = new PandaUsingAutoString4(23, "Paul");
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
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = jc.save(new PandaUsingAutoString4(25, "Jeff"));
        assertThat(p.id()).isNotNull().isNotEmpty();
    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        PandaUsingAutoString4 p = jc.save(new PandaUsingAutoString4(23, "Paul"));
        p.age = 24;
        PandaUsingAutoString4 p2 = jc.save(p);
        assertThat(p.id()).isEqualTo(p2.id());
    }

    @Test
    public void testSaveIterable() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        List<PandaUsingAutoString4> list = new ArrayList<>();
        PandaUsingAutoString4 p = (new PandaUsingAutoString4(23, "Paul"));
        PandaUsingAutoString4 p2 = jc.save(new PandaUsingAutoString4(13, "Paula"));
        PandaUsingAutoString4 p3 = jc.save(new PandaUsingAutoString4(21, "Pam"));
        p3.age = 45;
        list.add(p);
        list.add(p2);
        list.add(p3);
        Iterable<PandaUsingAutoString4> it2 =jc.save(list);
        assertThat(list).containsExactlyElementsOf(it2);
    }

    @Test
    public void testCountItemsInCollection() throws Exception {
        JongoCRUD<PandaUsingAutoString4, String> jc = new JongoCRUDService<>(PandaUsingAutoString4.class, db());
        long count = jc.count();
        jc.save(new PandaUsingAutoString4(23, "Paul"));
        jc.save(new PandaUsingAutoString4(23, "pam"));
        long newCount = jc.count();
        assertThat(newCount).isEqualTo(count + 2);
    }
}

