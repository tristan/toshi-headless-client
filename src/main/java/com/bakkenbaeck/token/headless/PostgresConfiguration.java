package com.bakkenbaeck.token.headless;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class PostgresConfiguration {
    private String url;
    private String jdbcUrl;
    private String username;
    private String password;
    private String sslmode;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        URI dbUri = null;
        try {
            dbUri = new URI(url);
            if (dbUri.getUserInfo() != null) {
                if (dbUri.getUserInfo().contains(":")) {
                    this.username = dbUri.getUserInfo().split(":")[0];
                    this.password = dbUri.getUserInfo().split(":")[1];
                } else {
                    this.username = dbUri.getUserInfo();
                    this.password = "";
                }
            }
            int port = dbUri.getPort();
            if (port == -1) {
                port = 5432;
            }
            this.jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + port + dbUri.getPath();
            // check for stores sslmode
            String query = dbUri.getQuery();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                    if (key.equals("sslmode")) {
                        this.sslmode = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                        break;
                    }
                }
            }
            if (this.sslmode != null) {
                this.jdbcUrl += "?sslmode=" + this.sslmode;
            }
        } catch (URISyntaxException|UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getSslmode() {
        return this.sslmode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSslmode(String sslmode) {
        this.sslmode = sslmode;
        if (this.url != null) {
            // reset the url to include sslmode
            this.setUrl(this.url);
        }
    }

    public void setEnvKey(String envKey) {
        setUrl(System.getenv(envKey));
    }

    public PostgresConfiguration() {}
}
