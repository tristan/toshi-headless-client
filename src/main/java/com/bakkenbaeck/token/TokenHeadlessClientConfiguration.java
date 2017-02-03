package com.bakkenbaeck.token;


public final class TokenHeadlessClientConfiguration {
    private String address;
    private String server;
    private String seed;
    private String store;
    private RedisConfiguration redis;

    public String getAddress() {
        return (address != null) ? address : System.getenv("TOKEN_CLIENT_ADDRESS");
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSeed() {
        return (seed != null) ? seed : System.getenv("TOKEN_CLIENT_SEED");
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

    public RedisConfiguration getRedis() {
        return redis;
    }

    public void setRedis(RedisConfiguration redis) {
        this.redis = redis;
    }
}