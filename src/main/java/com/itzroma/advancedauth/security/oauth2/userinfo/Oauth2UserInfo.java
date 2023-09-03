package com.itzroma.advancedauth.security.oauth2.userinfo;

import java.util.Map;

public abstract class Oauth2UserInfo {
    protected final Map<String, Object> attributes;

    protected Oauth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getFirstName();

    public abstract String getLastName();

    public abstract String getEmail();

    public abstract String imageUrl();
}
