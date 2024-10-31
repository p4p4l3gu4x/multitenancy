package com.hudson.multitenancy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfiguration {

    @Value("${application.database.url}")
    private String url;

    @Value("${application.database.user}")
    private String user;

    @Value("${application.database.password}")
    private String password;

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
