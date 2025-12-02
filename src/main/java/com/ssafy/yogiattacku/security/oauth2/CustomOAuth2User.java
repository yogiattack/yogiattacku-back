package com.ssafy.yogiattacku.security.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final String socialId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.socialId = String.valueOf(attributes.get("id"));
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        this.email = (String) account.get("email");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        this.nickname = (String) profile.get("nickname");
        this.profileImageUrl = (String) profile.get("profile_image_url");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return socialId;
    }
}
