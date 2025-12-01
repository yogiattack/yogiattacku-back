package com.ssafy.yogiattacku.user.service;

import com.ssafy.yogiattacku.user.entity.User;
import com.ssafy.yogiattacku.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User upsertFromKakao(String socialId, String email, String nickname, String profileImageUrl) {
        return userRepository.findBySocialId(socialId)
                .map(existing -> syncIfChanged(existing, email, nickname, profileImageUrl))
                .orElseGet(() -> register(socialId, email, nickname, profileImageUrl));
    }

    private User syncIfChanged(User user, String email, String nickname, String profileImageUrl) {
        if(!Objects.equals(user.getEmail(), email)) {
            user.changeEmail(email);
        }

        String verifyNickname = resolveNickname(email, nickname);
        if(!Objects.equals(verifyNickname, user.getNickname())) {
            user.changeNickname(verifyNickname);
        }

        if(!Objects.equals(profileImageUrl, user.getProfileImageUrl())) {
            user.changeProfileImageUrl(profileImageUrl);
        }
        return user;
    }

    private User register(String socialId, String email, String nickname, String profileImageUrl) {
        String verifyNickname = resolveNickname(email, nickname);
        return userRepository.save(User.of(socialId, email, nickname, profileImageUrl));
    }


    private String resolveNickname(String email, String nickname) {
        if (nickname != null && !nickname.isBlank()) {
            return nickname;
        }
        return email.substring(0, email.indexOf("@"));
    }
}
