package carehub.domain.user;

import carehub.common.exception.BusinessException;
import carehub.common.exception.ErrorCode;
import carehub.common.service.EmailService;
import carehub.domain.security.VerificationService;
import carehub.web.dto.auth.request.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * 이메일 존재 여부 확인
     * @param email 확인할 이메일
     * @return 존재 여부
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 이메일 인증 코드 발송
     * @param email 인증 코드를 발송할 이메일
     */
    public void sendVerificationCode(String email) {
        if (isEmailExists(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String verificationCode = verificationService.createVerificationCode(email);
        emailService.sendVerificationEmail(email, verificationCode);

        log.info("Verification code sent to email: {}", email);
    }

    /**
     * 이메일 인증 코드 확인
     * @param email 확인할 이메일
     * @param code 확인할 인증 코드
     * @return 인증 토큰
     */
    public String verifyEmailCode(String email, String code) {
        boolean isVerified = verificationService.verifyCode(email, code);

        if (!isVerified) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        return verificationService.createVerificationToken(email);
    }

    /**
     * 사용자 등록 (회원가입)
     * @param request 회원가입 요청 객체
     * @return 생성된 사용자 ID
     */
    @Transactional
    public Long registerUser(RegisterUserRequest request) {
        String email = verificationService.verifyToken(request.getVerificationToken());

        if (email == null || !email.equals(request.getEmail())) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

        if (isEmailExists(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .isActive(true)
                .build();

        UserPreference preference = new UserPreference();
        preference.setUser(user);
        preference.setTheme("light");
        preference.setLanguage("ko");

        user.setPreference(preference);

        User savedUser = userRepository.save(user);
        verificationService.completeVerification(email, request.getVerificationToken());

        emailService.sendWelcomeEmail(email, savedUser.getName());
        return savedUser.getId();
    }

}
