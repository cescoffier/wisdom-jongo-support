package org.wisdom.jongo.bridge;

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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Starts and stops mongo.
 */
public class JongoBaseTest {

    private static int port;
    private static MongodExecutable mongodExecutable;
    private static MongodProcess mongod;
    private static MongoClient client;

    public static MongoClient createClient() {
        if (client != null) {
            return client;
        }
        try {
            return new MongoClient("localhost", port);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Cannot connect to localhost", e);
        }
    }

    public DB db() {
        return createClient().getDB("TestDatabase");
    }

    @BeforeClass
    public static void startMongo() throws IOException {
        port = Network.getFreeServerPort();
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();
        mongodExecutable = starter.prepare(mongodConfig);
        mongod = mongodExecutable.start();
        client = createClient();
    }

    @AfterClass
    public static void stopMongo() {
        if (mongod != null){
            mongod.stop();
        }
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
        client = null;
    }
}
