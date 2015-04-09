//todo cant inclue the class in this file because it puts a dollar sign in the name?
//todo: needs to use the embedded mongo,
package sample;

import com.google.common.collect.Iterables;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class JongoCRUDServiceTest {
    private MongodStarter starter;
    private IMongodConfig mongodConfig;
    private MongodExecutable mongodExecutable;
    private MongodProcess mongod;

    private static int port;

    @BeforeClass
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
    }

    @Test
    public void testGetEntityClass() throws Exception {

        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUDService<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);

        assertThat(jc.getEntityClass()).isEqualTo(sample.Panda.class);

    }

    @Test
    public void testGetIdClass() throws Exception {
       DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUDService<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        assertThat(jc.getIdClass()).isEqualTo(String.class);

    }

    @Test
    public void testGetJongoDataStore() throws Exception {

    }


    @Test
    public void testFindOneByID() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Panda p = jc.save(new Panda(23, "Paul"));
        Panda p2 = jc.findOne(p._id);
        assertThat(p._id).isEqualTo(p2._id);

    }

   @Test
    public void testFindOneByIdNonExsisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Panda p2 = jc.findOne("1234");
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
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Long count =jc.count();
        Long sum = 0L;
        Iterable<Panda> iterable = jc.findAll();
       sum= Long.valueOf(Iterables.size(iterable));
        assertThat(count).isEqualTo(sum);


    }

    @Test
    public void testFindAll1() throws Exception {

    }

    @Test
    public void testFindAll2() throws Exception {

    }

    @Test
    public void testExists() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Panda p = jc.save(new Panda(23, "Paul"));
        assertThat(jc.exists(p._id)).isEqualTo(true);
    }

    @Test
    public void testDoesNotExist() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Panda p = jc.save(new Panda(23, "Paul"));
        assertThat(jc.exists("1234")).isEqualTo(false);

    }

    @Test
    public void testDeleteByIdExisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Panda p = jc.save(new Panda(23, "Paul"));
        Long count = jc.count();
        jc.delete(p._id);
        Long count2 = jc.count();
        assertThat(count).isEqualTo(count2+1);
        assertThat(jc.findOne(p._id)).isNull();
    }


    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Panda p = jc.save(new Panda(23, "Paul"));
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
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Iterable<Panda> iterable =jc.findAll();
        jc.delete(iterable);
        assertThat(jc.count()).isEqualTo(0L);

    }

    @Test
    public void testDelete2() throws Exception {

    }

    @Test
    public void testSaveNewEntity() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Panda f = jc.save(new Panda(25, "Jeff"));
        assertThat(f._id).isNotNull().isNotEmpty();

    }

    @Test
    public void testSaveExistingEntity() throws Exception {
        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        Panda p = jc.save(new Panda(23, "Paul"));
        p.age = 24;
        Panda p2 = jc.save(p);
        assertThat(p._id).isEqualTo(p2._id);

    }

    @Test
    public void testCountItemsInCollection() throws Exception {

        DB db = new MongoClient().getDB("TestDatabase");
        JongoCRUD<Panda> jc = new JongoCRUDService<Panda>(Panda.class, db);
        long count = jc.count();
        jc.save(new Panda(23, "Paul"));
        jc.save(new Panda(23, "pam"));
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

