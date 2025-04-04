package carehub.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String applicationName = "Care Hub";

    /**
     * 이메일 인증 코드 발송
     * @param to 수신자 이메일
     * @param verificationCode 인증 코드
     */
    @Async
    public void sendVerificationEmail(String to, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED,
                    StandardCharsets.UTF_8.name()
            );

            Context context = new Context();
            context.setVariable("verificationCode", verificationCode);
            context.setVariable("appName", applicationName);

            String emailContent = templateEngine.process("email/verification-code", context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(applicationName + " - 이메일 인증 코드");
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 회원가입 완료 이메일 발송
     * @param to 수신자 이메일
     * @param name 사용자 이름
     */
    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED,
                    StandardCharsets.UTF_8.name()
            );

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("appName", applicationName);

            String emailContent = templateEngine.process("email/welcome", context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(applicationName + " - 회원가입을 환영합니다");
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", to, e);
        }
    }
}
