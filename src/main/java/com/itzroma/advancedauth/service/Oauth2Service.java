package com.itzroma.advancedauth.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface Oauth2Service {
    OAuth2User processOauth2Auth(OAuth2UserRequest userRequest);

    OidcUser processOidcAuth(OidcUserRequest userRequest);
}
