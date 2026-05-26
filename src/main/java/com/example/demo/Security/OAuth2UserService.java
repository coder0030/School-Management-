package com.example.demo.Security;

import com.example.demo.DTO.LoginResponseDTO;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.AuthProviderType;
import com.example.demo.Helper.Role;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2UserService {

    private final AuthUtil authUtil;
    private final UserRepository userRepository;

    public LoginResponseDTO processOAuthPostLogin(OAuth2User oAuth2User, String registrationId) {
        AuthProviderType providerType = authUtil.getProviderType(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        User user;
        User providerUser = userRepository.findByProviderIdAndProviderType(providerId, String.valueOf(providerType))
               .orElse(null);

       String email = oAuth2User.getAttribute("email");
       User emailUser = userRepository.findByUsername(email).orElse(null);

       if(providerUser != null) {
           user = providerUser;
           user.setLastLoginAt(LocalDateTime.now());
           user = userRepository.save(user);

       } else if(providerUser == null && emailUser == null) {
           String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);

           user = User.builder()
                   .username(username)
                   .providerType(providerType)
                   .providerId(providerId)
                   .roles(Set.of(Role.ROLE_USER))
                   .createdAt(LocalDateTime.now())
                   .lastLoginAt(LocalDateTime.now())
                   .isActive(true)
                   .build();

           user = userRepository.save(user);

       } else if(emailUser != null && emailUser.getProviderType() == null) {
            emailUser.setProviderId(providerId);
            emailUser.setProviderType(providerType);
            emailUser.setActive(true);
           emailUser.setLastLoginAt(LocalDateTime.now());

           user = userRepository.save(emailUser);

       } else if(emailUser != null && emailUser.getProviderType() != null) {

            if(emailUser.getProviderType().equals(providerType)) {
                user = emailUser;
                user.setLastLoginAt(LocalDateTime.now());
                user = userRepository.save(user);

            } else {
                throw new BadCredentialsException(
                        String.format("Email %s is already registered with %s. " +
                                        "Please login with %s or delete your account to use %s.", email, emailUser.getProviderType(),
                                emailUser.getProviderType(), providerType)
                );
            }

       } else {
           throw new BadCredentialsException("Unable to process OAuth login. Please contact support.");
       }

        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDTO(token, user.getId());
    }
}
