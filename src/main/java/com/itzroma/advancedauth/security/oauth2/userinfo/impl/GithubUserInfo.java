package com.itzroma.advancedauth.security.oauth2.userinfo.impl;

import com.itzroma.advancedauth.security.oauth2.userinfo.Oauth2UserInfo;
import com.itzroma.advancedauth.util.FullName;

import java.util.Map;

public class GithubUserInfo extends Oauth2UserInfo {
    private final FullName fullName;

    public GithubUserInfo(Map<String, Object> attributes) {
        super(attributes);
        fullName = FullName.fromName((String) attributes.get("name"));
    }

    @Override
    public String getFirstName() {
        return fullName.firstName();
    }

    @Override
    public String getLastName() {
        return fullName.lastName();
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String imageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
