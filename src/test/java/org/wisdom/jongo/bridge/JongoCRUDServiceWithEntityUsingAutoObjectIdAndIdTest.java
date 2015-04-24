
package org.wisdom.jongo.bridge;

import com.google.common.collect.Iterables;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.jongo.entities.PandaUsingAutoObjectIdAndId5;
import org.wisdom.jongo.service.JongoCRUD;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class JongoCRUDServiceWithEntityUsingAutoObjectIdAndIdTest extends JongoBaseTest {

    @Before
    public void clearDb() {
        JongoCRUDService<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        jc.deleteAllFromCollection();
    }

    @Test
    public void testGetEntityClass() throws Exception {
        JongoCRUDService<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        assertThat(jc.getEntityClass()).isEqualTo(PandaUsingAutoObjectIdAndId5.class);
    }

    @Test
    public void testGetIdClass() throws Exception {
        JongoCRUDService<PandaUsingAutoObjectIdAndId5, String> jc
                = new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        assertThat(jc.getIdClass()).isEqualTo(String.class);
    }

    @Test
    public void testFindOneByID() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        PandaUsingAutoObjectIdAndId5 p = jc.save(new PandaUsingAutoObjectIdAndId5(23, "Paul"));
        PandaUsingAutoObjectIdAndId5 p2 = jc.findOne(p.getKey());
        assertThat(p.getKey()).isEqualTo(p2.getKey());
    }

    @Test
    public void testFindOneByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        PandaUsingAutoObjectIdAndId5 p2 = jc.findOne("1234");
        assertThat(p2).isNull();
    }

    @Test
    public void testFindAll() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        Long count = jc.count();
        Iterable<PandaUsingAutoObjectIdAndId5> iterable = jc.findAll();
        assertThat(count).isEqualTo(Iterables.size(iterable));
    }

    @Test
    public void testExists() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        PandaUsingAutoObjectIdAndId5 p = jc.save(new PandaUsingAutoObjectIdAndId5(23, "Paul"));
        assertThat(jc.exists(p.getKey())).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc = new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        jc.save(new PandaUsingAutoObjectIdAndId5(23, "Paul"));
        assertThat(jc.exists("1234")).isEqualTo(false);
    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        PandaUsingAutoObjectIdAndId5 p = jc.save(new PandaUsingAutoObjectIdAndId5(23, "Paul"));
        Long count = jc.count();
        jc.delete(p.getKey());
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.getKey())).isNull();
    }


    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc = new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        jc.save(new PandaUsingAutoObjectIdAndId5(23, "Paul"));
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
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());

        for (int i = 0; i < 5; i++) {
            jc.save(new PandaUsingAutoObjectIdAndId5(i, "Paul"));
        }
        Iterable<PandaUsingAutoObjectIdAndId5> iterable = jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);

    }

    @Test
    public void testDeleteIterableWithNonExisting() throws Exception {

        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc
                = new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        List<PandaUsingAutoObjectIdAndId5> list = new ArrayList<PandaUsingAutoObjectIdAndId5>();
        Long count = jc.count();
        list.add(jc.save(new PandaUsingAutoObjectIdAndId5(1, "Paul")));
        list.add(new PandaUsingAutoObjectIdAndId5(5, "Paul"));
        list.add(jc.save(new PandaUsingAutoObjectIdAndId5(2, "Paul")));
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
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc = new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        PandaUsingAutoObjectIdAndId5 p = jc.save(new PandaUsingAutoObjectIdAndId5(23, "Paul"));
        Long count = jc.count();
        jc.delete(p);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2 + 1);
        assertThat(jc.findOne(p.getKey())).isNull();
    }

    @Test
    public void testDeleteEntityNonExisting() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        PandaUsingAutoObjectIdAndId5 p = new PandaUsingAutoObjectIdAndId5(23, "Paul");
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
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        PandaUsingAutoObjectIdAndId5 p = jc.save(new PandaUsingAutoObjectIdAndId5(25, "Jeff"));
        assertThat(p.getKey()).isNotNull().isNotEmpty();
    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        PandaUsingAutoObjectIdAndId5 p = jc.save(new PandaUsingAutoObjectIdAndId5(23, "Paul"));
        p.age = 24;
        PandaUsingAutoObjectIdAndId5 p2 = jc.save(p);
        assertThat(p.getKey()).isEqualTo(p2.getKey());
    }

    @Test
    public void testSaveIterable() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        List<PandaUsingAutoObjectIdAndId5> list = new ArrayList<>();
        PandaUsingAutoObjectIdAndId5 p = (new PandaUsingAutoObjectIdAndId5(23, "Paul"));
        PandaUsingAutoObjectIdAndId5 p2 = jc.save(new PandaUsingAutoObjectIdAndId5(13, "Paula"));
        PandaUsingAutoObjectIdAndId5 p3 = jc.save(new PandaUsingAutoObjectIdAndId5(21, "Pam"));
        p3.age = 45;
        list.add(p);
        list.add(p2);
        list.add(p3);
        Iterable<PandaUsingAutoObjectIdAndId5> it2 = jc.save(list);
        assertThat(list).containsExactlyElementsOf(it2);
    }

    @Test
    public void testCountItemsInCollection() throws Exception {
        JongoCRUD<PandaUsingAutoObjectIdAndId5, String> jc =
                new JongoCRUDService<>(PandaUsingAutoObjectIdAndId5.class, db());
        long count = jc.count();
        jc.save(new PandaUsingAutoObjectIdAndId5(23, "Paul"));
        jc.save(new PandaUsingAutoObjectIdAndId5(23, "pam"));
        long newCount = jc.count();
        assertThat(newCount).isEqualTo(count + 2);
    }

}

