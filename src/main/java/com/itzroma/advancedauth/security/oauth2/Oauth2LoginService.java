package com.itzroma.advancedauth.security.oauth2;

import com.itzroma.advancedauth.model.Role;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.repository.UserRepository;
import com.itzroma.advancedauth.security.AuthProvider;
import com.itzroma.advancedauth.security.AuthUserDetails;
import com.itzroma.advancedauth.service.RoleService;
import com.itzroma.advancedauth.util.FullName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Oauth2LoginService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;

    public OAuth2User processOauth2Auth(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        FullName fullName = FullName.fromName(Objects.requireNonNull(oAuth2User.getAttribute("name")));
        AuthUserDetails userDetails = AuthUserDetails.builder()
                .firstName(fullName.firstName())
                .lastName(fullName.lastName())
                .attributes(oAuth2User.getAttributes())
                .authorities(oAuth2User.getAuthorities())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .email(oAuth2User.getAttribute("email"))
                .enabled(true)
                .imageUrl(oAuth2User.getAttribute("avatar_url"))
                .authProvider(extractAuthProvider(userRequest))
                .build();
        saveOrUpdate(userDetails);
        return userDetails;
    }

    private static AuthProvider extractAuthProvider(OAuth2UserRequest userRequest) {
        return AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
    }

    private void saveOrUpdate(AuthUserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getEmail());
        User user;
        if (userOptional.isPresent()) { // update existing
            user = userOptional.get();
            if (user.getAuthProvider() != userDetails.getAuthProvider()) {
                throw new IllegalStateException("This user registered with " + user.getAuthProvider() + " provider");
            }

            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setImageUrl(userDetails.getImageUrl());
        } else { // save new
            user = new User(
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getEmail(),
                    userDetails.getPassword(),
                    userDetails.getEnabled(),
                    userDetails.getImageUrl(),
                    userDetails.getAuthProvider()
            );
            user.getRoles().add(roleService.findByRoleName(Role.RoleName.USER));
        }
        userRepository.save(user);
    }

    public OidcUser processOidcAuth(OidcUserRequest userRequest) {
        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);
        AuthUserDetails userDetails = AuthUserDetails.builder()
                .firstName(oidcUser.getGivenName())
                .lastName(oidcUser.getFamilyName())
                .attributes(oidcUser.getAttributes())
                .authorities(oidcUser.getAuthorities())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .email(oidcUser.getEmail())
                .enabled(true)
                .claims(oidcUser.getClaims())
                .userInfo(oidcUser.getUserInfo())
                .idToken(oidcUser.getIdToken())
                .imageUrl(oidcUser.getPicture())
                .authProvider(extractAuthProvider(userRequest))
                .build();
        saveOrUpdate(userDetails);
        return userDetails;
    }
}
