package com.itzroma.advancedauth.security.oauth2;

import com.itzroma.advancedauth.exception.Oauth2AuthenticationProcessingException;
import com.itzroma.advancedauth.model.Role;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.repository.UserRepository;
import com.itzroma.advancedauth.security.AuthProvider;
import com.itzroma.advancedauth.security.AuthUserDetails;
import com.itzroma.advancedauth.security.oauth2.userinfo.Oauth2UserInfo;
import com.itzroma.advancedauth.security.oauth2.userinfo.Oauth2UserInfoFactory;
import com.itzroma.advancedauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class Oauth2LoginService extends DefaultOAuth2UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.debug("Can't process oauth2 user " + oAuth2User.getName());
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        AuthProvider authProvider = AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        Oauth2UserInfo oauth2UserInfo = Oauth2UserInfoFactory.getOauth2UserInfo(authProvider, oAuth2User.getAttributes());

        if (!StringUtils.hasText(oauth2UserInfo.getEmail())) {
            throw new Oauth2AuthenticationProcessingException(
                    "Can't obtain an email from " + authProvider + ". Try to go to their privacy settings."
            );
        }

        AuthUserDetails userDetails = AuthUserDetails.builder()
                .firstName(oauth2UserInfo.getFirstName())
                .lastName(oauth2UserInfo.getLastName())
                .attributes(oAuth2User.getAttributes())
                .authorities(oAuth2User.getAuthorities())
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // mock password
                .email(oauth2UserInfo.getEmail())
                .enabled(true)
                .imageUrl(oauth2UserInfo.imageUrl())
                .authProvider(authProvider)
                .build();
        saveOrUpdate(userDetails);
        return userDetails;
    }

    private void saveOrUpdate(AuthUserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getEmail());
        User user;
        if (userOptional.isPresent()) { // update existing
            user = userOptional.get();
            if (user.getAuthProvider() != userDetails.getAuthProvider()) {
                throw new Oauth2AuthenticationProcessingException("This user registered with " + user.getAuthProvider() + " provider");
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
                    userDetails.isEnabled(),
                    userDetails.getImageUrl(),
                    userDetails.getAuthProvider()
            );
            user.getRoles().add(roleService.findByRoleName(Role.RoleName.USER));
        }
        userRepository.save(user);
    }
}
