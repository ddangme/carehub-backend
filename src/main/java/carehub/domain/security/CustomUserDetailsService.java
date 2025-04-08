package carehub.domain.security;

import carehub.domain.user.User;
import carehub.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.contains("@")) {
            return userRepository.findByEmail(username)
                    .map(this::createUserDetails)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        }

        try {
            Long userId = Long.parseLong(username);
            return userRepository.findById(userId)
                    .map(this::createUserDetails)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("유효하지 않은 사용자 식별자: " + username);
        }
    }

    private UserDetails createUserDetails(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPassword(),
                user.isActive(),
                true, // 계정 만료 여부
                true, // 자격 증명 만료 여부
                true, // 계정 잠금 여부
                authorities
        );
    }
}