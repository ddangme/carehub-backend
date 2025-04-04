package carehub.domain.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String CODE_PREFIX = "verification:code:";
    private static final String TOKEN_PREFIX = "verification:token:";

    private static final int CODE_LENGTH = 6;
    private static final long EXPIRATION_TIME = 5;

    private String createRandomNumericCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    public String createVerificationCode(String email) {
        String code = createRandomNumericCode(CODE_LENGTH);

        redisTemplate.opsForValue().set(
                CODE_PREFIX + email,
                code,
                EXPIRATION_TIME,
                TimeUnit.MINUTES
        );

        return code;
    }

    public String createVerificationToken(String email) {
        String token = UUID.randomUUID().toString();;

        redisTemplate
                .opsForValue()
                .set(
                        TOKEN_PREFIX + token,
                        email,
                        EXPIRATION_TIME,
                        TimeUnit.MINUTES
                );

        return token;
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(CODE_PREFIX + email);
        return storedCode != null && storedCode.equals(code);
    }

    public String verifyToken(String token) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }

    public void completeVerification(String email, String token) {
        redisTemplate.delete(CODE_PREFIX + email);
        if (token != null) {
            redisTemplate.delete(TOKEN_PREFIX + token);
        }
    }

}
