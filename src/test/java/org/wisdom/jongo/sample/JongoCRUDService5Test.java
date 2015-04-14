
//todo: needs to use the embedded mongo,
package org.wisdom.jongo.sample;

import com.google.common.collect.Iterables;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class JongoCRUDService5Test {
    private MongodStarter starter;
    private IMongodConfig mongodConfig;
    private MongodExecutable mongodExecutable;
    private MongodProcess mongod;

    private static int port;

   /* @BeforeClass
    public static void retrieveAFreePort() throws IOException {
        port = Network.getFreeServerPort();
    }

    @Before
    public void startMongo() throws IOException {
        starter = MongodStarter.getDefaultInstance();
        mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();
        mongodExecutable = starter.prepare(mongodConfig);
        mongod = mongodExecutable.start();
    }

    @After
    public void stopMongo() {
        if (mongod != null){
            mongod.stop();
        }
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }*/

    @Test
    public void testGetEntityClass() throws Exception {

        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUDService<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);

        assertThat(jc.getEntityClass()).isEqualTo(Panda5_ObjID_ID_StringKey.class);

    }

    @Test
    public void testGetIdClass() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUDService<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        assertThat(jc.getIdClass()).isEqualTo(String.class);

    }

    @Test
    public void testGetJongoDataStore() throws Exception {

    }


    @Test
    public void testFindOneByID() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = jc.save(new Panda5_ObjID_ID_StringKey(23, "Paul"));
        Panda5_ObjID_ID_StringKey p2 = jc.findOne(p.get_id());
        assertThat(p.get_id()).isEqualTo(p2.get_id());

    }

    @Test
    public void testFindOneByIdNonExsisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey,String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p2 = jc.findOne("1234");
        assertThat(p2).isNull();

    }

    @Test
    public void testFindOne1() throws Exception {

    }

    /*todo all of the tests should be independent, this tests needs two versions, one where the db has stuff another where it is empty
     however at this moment the database is polluted with the data from the other tests this should be fixed when using embedded mongo*/
    @Test
    public void testFindAll() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Long count =jc.count();
        Long sum = 0L;
        Iterable<Panda5_ObjID_ID_StringKey> iterable = jc.findAll();
        sum= Long.valueOf(Iterables.size(iterable));
        assertThat(count).isEqualTo(sum);


    }

    @Test
    public void testFindAllByIterable() throws Exception {

    }

    @Test
    public void testFindAllByEntityFilter() throws Exception {

    }

    @Test
    public void testExists() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = jc.save(new Panda5_ObjID_ID_StringKey(23, "Paul"));
        assertThat(jc.exists(p.get_id())).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = jc.save(new Panda5_ObjID_ID_StringKey(23, "Paul"));
        assertThat(jc.exists("1234")).isEqualTo(false);

    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = jc.save(new Panda5_ObjID_ID_StringKey(23, "Paul"));
        Long count = jc.count();
        jc.delete(p.get_id());
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2+1);
        assertThat(jc.findOne(p.get_id())).isNull();
    }


    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = jc.save(new Panda5_ObjID_ID_StringKey(23, "Paul"));
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



    public void testDeleteIterable() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);

        for (int i= 0; i<5;i++){
            jc.save(new Panda5_ObjID_ID_StringKey(i, "Paul"));
        }
        Iterable<Panda5_ObjID_ID_StringKey> iterable =jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);

    }


    public void testDeleteIterableWithNonExsisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        List<Panda5_ObjID_ID_StringKey> list = new ArrayList<Panda5_ObjID_ID_StringKey>();
        Long count = jc.count();
        list.add(jc.save(new Panda5_ObjID_ID_StringKey(1, "Paul")));
        list.add(new Panda5_ObjID_ID_StringKey(5, "Paul"));
        list.add(jc.save(new Panda5_ObjID_ID_StringKey(2, "Paul")));
        Iterable<Panda5_ObjID_ID_StringKey> iterable =list;
        try {
            jc.delete(iterable);
            fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK, the error is expected.
        }

        assertThat(count).isEqualTo(jc.count());
        //todo fails see todo for this method
    }

    @Test
    public void testDeleteEntity() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = jc.save(new Panda5_ObjID_ID_StringKey(23, "Paul"));
        Long count = jc.count();
        jc.delete(p);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2+1);
        assertThat(jc.findOne(p.get_id())).isNull();
    }


    public void testDeleteEntityNonExisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = new Panda5_ObjID_ID_StringKey(23, "Paul");
        Long count = jc.count();
        try {
            jc.delete(p);
            fail("Illegal Argument Exception expected");
        } catch (IllegalArgumentException e) {
            // OK, the error is expected.
        }
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2);
        assertThat(jc.findOne(p.get_id())).isNull();
    }

    @Test
    public void testSaveNewEntity() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = jc.save(new Panda5_ObjID_ID_StringKey(25, "Jeff"));
        assertThat(p.get_id()).isNotNull().isNotEmpty();

    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        Panda5_ObjID_ID_StringKey p = jc.save(new Panda5_ObjID_ID_StringKey(23, "Paul"));
        p.age = 24;
        Panda5_ObjID_ID_StringKey p2 = jc.save(p);
        assertThat(p.get_id()).isEqualTo(p2.get_id());

    }

    @Test
    public void testSaveIterable() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        List<Panda5_ObjID_ID_StringKey> list = new ArrayList<Panda5_ObjID_ID_StringKey>();
        Panda5_ObjID_ID_StringKey p = (new Panda5_ObjID_ID_StringKey(23, "Paul"));
        Panda5_ObjID_ID_StringKey p2 = jc.save(new Panda5_ObjID_ID_StringKey(13, "Paula"));
        Panda5_ObjID_ID_StringKey p3 = jc.save(new Panda5_ObjID_ID_StringKey(21, "Pam"));
        p3.age = 45;
        list.add(p);
        list.add(p2);
        list.add(p3);
        Iterable<Panda5_ObjID_ID_StringKey> iterable = list;
        Iterable<Panda5_ObjID_ID_StringKey> it2 =jc.save(iterable);
        assertThat(iterable).containsExactlyElementsOf(it2);




    }

    @Test
    public void testCountItemsInCollection() throws Exception {

        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda5_ObjID_ID_StringKey, String> jc = new JongoCRUDService<>(Panda5_ObjID_ID_StringKey.class, db);
        long count = jc.count();
        jc.save(new Panda5_ObjID_ID_StringKey(23, "Paul"));
        jc.save(new Panda5_ObjID_ID_StringKey(23, "pam"));
        long newCount = jc.count();
        assertThat(newCount).isEqualTo(count + 2);

    }

    @Test
    public void testGetRepository() throws Exception {
         }

    @Test
    public void testExecuteTransactionalBlock() throws Exception {

    }

    @Test
    public void testGetTransactionManager() throws Exception {

    }

    @Test
    public void testTransaction() throws Exception {

    }

    @Test
    public void testTransaction1() throws Exception {

    }

    @Test
    public void testExecuteTransactionalBlock1() throws Exception {

    }


}

