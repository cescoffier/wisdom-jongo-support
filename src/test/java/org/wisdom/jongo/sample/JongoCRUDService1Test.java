
//todo: needs to use the embedded mongo,
package org.wisdom.jongo.sample;

import com.google.common.collect.Iterables;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class JongoCRUDService1Test {
    private MongodStarter starter;
    private IMongodConfig mongodConfig;
    private MongodExecutable mongodExecutable;
    private MongodProcess mongod;

    private static int port;

   @Before
    public void clearDb(){
        DB db = null;
        try {
            db = new MongoClient().getDB("TestDatabase");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        JongoCRUDService<Panda6_objidId, org.bson.types.ObjectId> jc = new JongoCRUDService<>(Panda6_objidId.class, db);
        jc.deleteAllFromCollection();
    }

    @After
    public void clearDbA(){
        DB db = null;
        try {
            db = new MongoClient().getDB("TestDatabase");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        JongoCRUDService<Panda6_objidId, org.bson.types.ObjectId> jc = new JongoCRUDService<>(Panda6_objidId.class, db);
        jc.deleteAllFromCollection();
    }



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
        JongoCRUDService<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);

        assertThat(jc.getEntityClass()).isEqualTo(Panda1_idlong.class);

    }

    @Test
    public void testGetIdClass() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUDService<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        assertThat(jc.getIdClass()).isEqualTo(Long.class);

    }

    @Test
    public void testGetJongoDataStore() throws Exception {

    }


    @Test
    public void testFindOneByID() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(88L);
        jc.save(p);
        Panda1_idlong p2 = jc.findOne(p.get_id());
        assertThat(p.get_id()).isEqualTo(p2.get_id());

    }

    @Test
    public void testFindOneByIdNonExsisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong,Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p2 = jc.findOne(1234L);
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
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Long count =jc.count();
        Long sum = 0L;
        Iterable<Panda1_idlong> iterable = jc.findAll();
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
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);
        jc.save(p);
        assertThat(jc.exists(p.get_id())).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);
        jc.save(p);
        assertThat(jc.exists(1234L)).isEqualTo(false);

    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p =new Panda1_idlong(23, "Paul");
        p.set_id(189L);
        jc.save(p);
        Long count = jc.count();
        jc.delete(p.get_id());
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2+1);
        assertThat(jc.findOne(p.get_id())).isNull();
    }


    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);
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



    public void testDeleteIterable() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);

        for (int i= 0; i<5;i++){
            Panda1_idlong p = new Panda1_idlong(23, "Paul");
            p.set_id(400L+i);
            jc.save(p);
        }
        Iterable<Panda1_idlong> iterable =jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);

    }


    public void testDeleteIterableWithNonExsisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        List<Panda1_idlong> list = new ArrayList<Panda1_idlong>();
        Long count = jc.count();

        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);

        list.add(jc.save(p));

        Panda1_idlong p1 = new Panda1_idlong(23, "Paul");
        p.set_id(405L);
        list.add(p1);
        Panda1_idlong p2 = new Panda1_idlong(23, "Paul");
        p.set_id(402L);

        list.add(jc.save(p2));
        Iterable<Panda1_idlong> iterable =list;
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
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);
        jc.save(p);
        Long count = jc.count();
        jc.delete(p);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2+1);
        assertThat(jc.findOne(p.get_id())).isNull();
    }


    public void testDeleteEntityNonExisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);
        jc.save(p);
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
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);
        jc.save(p);
        assertThat(p.get_id()).isNotNull();

    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);
        jc.save(p);
        p.age = 24;
        Panda1_idlong p2 = jc.save(p);
        assertThat(p.get_id()).isEqualTo(p2.get_id());

    }

    @Test
    public void testSaveIterable() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        List<Panda1_idlong> list = new ArrayList<Panda1_idlong>();
        Panda1_idlong p = new Panda1_idlong(23, "Paul");
        p.set_id(400L);
        jc.save(p);
        Panda1_idlong p2 = new Panda1_idlong(23, "Paul2");
        p2.set_id(402L);
        jc.save(p2);
        Panda1_idlong p3 = new Panda1_idlong(23, "Paul3");
        p3.set_id(403L);
        jc.save(p3);
        p3.age = 45;
        list.add(p);
        list.add(p2);
        list.add(p3);
        Iterable<Panda1_idlong> iterable = list;
        Iterable<Panda1_idlong> it2 =jc.save(iterable);
        assertThat(iterable).containsExactlyElementsOf(it2);




    }

    @Test
    public void testCountItemsInCollection() throws Exception {

        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda1_idlong, Long> jc = new JongoCRUDService<>(Panda1_idlong.class, db);
        jc.deleteAllFromCollection();
        long count = jc.count();
        Panda1_idlong p1 =new Panda1_idlong(23, "Paul");
        p1.set_id(89L);
        jc.save(p1);
        Panda1_idlong p2 =new Panda1_idlong(23, "Paul");
        p2.set_id(90L);
        jc.save(p2);
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

