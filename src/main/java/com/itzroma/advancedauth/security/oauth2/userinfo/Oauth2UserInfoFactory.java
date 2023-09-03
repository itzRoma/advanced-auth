package com.itzroma.advancedauth.security.oauth2.userinfo;

import com.itzroma.advancedauth.exception.Oauth2AuthenticationProcessingException;
import com.itzroma.advancedauth.security.AuthProvider;
import com.itzroma.advancedauth.security.oauth2.userinfo.impl.GithubUserInfo;
import com.itzroma.advancedauth.security.oauth2.userinfo.impl.GoogleUserInfo;

import java.util.Map;

public class Oauth2UserInfoFactory {
    private Oauth2UserInfoFactory() {
    }

    public static Oauth2UserInfo getOauth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        return switch (authProvider) {
            case GITHUB -> new GithubUserInfo(attributes);
            case GOOGLE -> new GoogleUserInfo(attributes);
            default -> throw new Oauth2AuthenticationProcessingException(
                    "Login with " + authProvider + " not supported"
            );
        };
    }
}
