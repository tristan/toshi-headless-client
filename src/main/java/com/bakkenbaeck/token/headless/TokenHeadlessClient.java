package com.bakkenbaeck.token.headless;


import com.bakkenbaeck.token.crypto.HDWallet;
import com.bakkenbaeck.token.headless.rpc.HeadlessRPC;
import com.bakkenbaeck.token.headless.signal.Manager;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

class TokenHeadlessClient {
    private static TokenHeadlessClientConfiguration config;
    private static Logger logger = Logger.getLogger(TokenHeadlessClient.class.toString());


    public static void main(String[] args) throws Exception {

        // -- config
        String configPath = "config.yml";
        if( args.length == 1 ) {
            configPath = args[0];
        }
        Yaml yaml = new Yaml();
        try( InputStream in = Files.newInputStream( Paths.get( configPath ) ) ) {
            config = yaml.loadAs(in, TokenHeadlessClientConfiguration.class);
            System.out.println( config.toString() );
        } catch (Exception e) {
            System.out.println( "Error parsing configuration - " + e.getMessage() );
            return;
        }

        // Workaround for BKS truststore
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);

        HDWallet wallet = new HDWallet().init(config.getSeed());
        System.out.println("ID Address: " + wallet.getAddress());
        System.out.println("Wallet Address: " + wallet.getWalletAddress());

        final String username = wallet.getAddress();
        final boolean voice = false;
        String settingsPath = config.getStore();
        Manager m = new Manager(username, settingsPath, config.getServer());


        // -- redis
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(poolConfig, config.getRedis().getHost(), config.getRedis().getPort(), config.getRedis().getTimeout(), config.getRedis().getPassword());


        // -- eth service
        EthService ethService = new EthService(wallet);


        // -- rpc
        HeadlessRPC rpc = new HeadlessRPC(jedisPool, username, ethService);


        final RedisSubscriber subscriber = new RedisSubscriber(rpc, m);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis subscriberJedis = new Jedis(config.getRedis().getUri());
                try {
                    subscriberJedis.subscribe(subscriber, username, username+"_rpc_request");
                    System.out.println("Subscription ended.");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Subscribing failed.", e);
                }
            }
        }).start();


        // -- signal
        if (m.userExists()) {
            try {
                m.init();
            } catch (Exception e) {
                System.err.println("Error loading state file \"" + m.getFileName() + "\": " + e.getMessage());
                return;
            }
        }

        //register
        if (!m.userHasKeys()) {
            m.createNewIdentity();
            try {
                m.createEthereumAccount(wallet);
            } catch (IOException e) {
                System.err.println("Verify error: " + e.getMessage());
                return;
            }
        }


        if (!m.isRegistered()) {
            System.err.println("User is not registered.");
            return;
        }
        double timeout = 5;
        boolean returnOnTimeout = false;
        boolean ignoreAttachments = true;
        try {
            m.receiveMessages((long) (timeout * 1000), TimeUnit.MILLISECONDS, returnOnTimeout, ignoreAttachments, new ReceiveMessageHandler(m, jedisPool, username));
        } catch (IOException e) {
            System.err.println("Error while receiving messages: " + e.getMessage());
            return;
        } catch (AssertionError e) {
            handleAssertionError(e);
            return;
        }


        // -- cleanup
        jedisPool.destroy();

    }

    private static void handleAssertionError(AssertionError e) {
        System.err.println("Failed to send/receive message (Assertion): " + e.getMessage());
        e.printStackTrace();
        System.err.println("If you use an Oracle JRE please check if you have unlimited strength crypto enabled, see README");
    }


}