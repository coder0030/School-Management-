package com.example.demo.Security;

import com.example.demo.ENTITY.User;
import com.example.demo.Helper.AuthProviderType;
import com.example.demo.Helper.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AuthUtil {

    @Value("${JWT_SECRET:}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles",
                user.getRoles()
                        .stream()
                        .map(Role::name)
                        .toList()
        );

        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .claim("userId", user.getId())
                .signWith(getSecretKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 90))
                .compact();
    }


    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("roles", List.class);
    }


    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);

            boolean isExpired = claims.getExpiration().before(new Date());
            if (isExpired) {
                log.warn("JWT token is expired");
                return false;
            }

            return true;

        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("JWT token unsupported: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("JWT token malformed: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.warn("JWT signature invalid: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT token empty or null: {}", ex.getMessage());
        }

        return false;
    }


    public AuthProviderType getProviderType(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google"   -> AuthProviderType.GOOGLE;
            case "github"   -> AuthProviderType.GITHUB;
            case "facebook" -> AuthProviderType.FACEBOOK;
            case "email"    -> AuthProviderType.EMAIL;
            default -> throw new IllegalArgumentException(
                    "Unsupported OAuth2 provider: " + registrationId);
        };
    }

    public String determineProviderIdFromOAuth2User(OAuth2User oAuth2User,
                                                    String registrationId) {
        String providerId = switch (registrationId.toLowerCase()) {
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> oAuth2User.getAttribute("id").toString();
            default -> {
                log.error("Unsupported OAuth2 provider: {}", registrationId);
                throw new IllegalArgumentException(
                        "Unsupported OAuth2 provider: " + registrationId);
            }
        };

        if (providerId == null || providerId.isBlank()) {
            log.error("Unable to determine providerId for provider: {}", registrationId);
            throw new IllegalArgumentException(
                    "Unable to determine providerId for provider: " + registrationId);
        }

        return providerId;
    }

    public String determineUsernameFromOAuth2User(OAuth2User oAuth2User,
                                                  String registrationId,
                                                  String providerId) {
        String email = oAuth2User.getAttribute("email");

        if (email != null && !email.isBlank()) {  // ✅ || → && fix kiya
            return email;
        }

        return switch (registrationId.toLowerCase()) {
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> oAuth2User.getAttribute("id");
            default       -> providerId;
        };
    }
}