package com.bakkenbaeck.token.headless;


public final class TokenHeadlessClientConfiguration {
    private String server;
    private String token_ethereum_service_url;
    private String token_id_service_url;
    private String seed;
    private String store;
    private String stage;
    private String username;
    private String name;
    private String avatar;
    private RedisConfiguration redis;
    private StorageConfiguration storage;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSeed() {
        if (this.seed == null) {
            String seed = System.getenv("TOKEN_APP_SEED");
            if (seed != null) {
                // strip any quotes around the seed
                if ((seed.startsWith("\"") && seed.endsWith("\"")) || (seed.startsWith("'") && seed.endsWith("'"))) {
                    seed = seed.substring(1, seed.length() - 1);
                }
                this.seed = seed;
            }
        }
        return this.seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getStage() {
        return (stage != null) ? stage : System.getenv("STAGE");
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getUsername() {
        return (username != null) ? username : System.getenv("TOKEN_APP_USERNAME");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return (name != null) ? name : System.getenv("TOKEN_APP_NAME");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return (avatar != null) ? avatar : System.getenv("TOKEN_APP_AVATAR");
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public RedisConfiguration getRedis() {
        return redis;
    }

    public void setRedis(RedisConfiguration redis) {
        this.redis = redis;
    }

    public StorageConfiguration getStorage() {
        return storage;
    }

    public void setStorage(StorageConfiguration storage) {
        this.storage = storage;
    }

    public String getToken_ethereum_service_url() {
        return token_ethereum_service_url;
    }

    public void setToken_ethereum_service_url(String token_ethereum_service_url) {
        this.token_ethereum_service_url = token_ethereum_service_url;
    }

    public String getToken_id_service_url() {
        return token_id_service_url;
    }

    public void setToken_id_service_url(String token_id_service_url) {
        this.token_id_service_url = token_id_service_url;
    }
}
