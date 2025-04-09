package carehub.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * 소셜 로그인 제공자와 ID로 사용자 찾기
     * @param provider 소셜 로그인 제공자 (예: kakao)
     * @param socialId 소셜 로그인 ID
     * @return 찾은 사용자 (Optional)
     */
    Optional<User> findBySocialProviderAndSocialId(String provider, String socialId);

}