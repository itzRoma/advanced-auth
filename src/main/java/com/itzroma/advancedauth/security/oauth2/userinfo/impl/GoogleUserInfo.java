package com.itzroma.advancedauth.security.oauth2.userinfo.impl;

import com.itzroma.advancedauth.security.oauth2.userinfo.Oauth2UserInfo;

import java.util.Map;

public class GoogleUserInfo extends Oauth2UserInfo {
    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getFirstName() {
        return (String) attributes.get("given_name");
    }

    @Override
    public String getLastName() {
        return (String) attributes.get("family_name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String imageUrl() {
        return (String) attributes.get("picture");
    }
}
