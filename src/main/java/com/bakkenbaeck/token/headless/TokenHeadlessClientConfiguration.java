package com.bakkenbaeck.token.headless;


public final class TokenHeadlessClientConfiguration {
    private String address;
    private String server;
    private String seed;
    private String store;
    private String username;
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

    public String getUsername() {
        return (username != null) ? username : System.getenv("TOKEN_CLIENT_USERNAME");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RedisConfiguration getRedis() {
        return redis;
    }

    public void setRedis(RedisConfiguration redis) {
        this.redis = redis;
    }
}