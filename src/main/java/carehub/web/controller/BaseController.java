package carehub.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class BaseController {

    /**
     * 현재 로그인한 사용자 ID 조회
     * @return 사용자 ID
     * @throws RuntimeException 인증되지 않은 경우
     */
    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User principal =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            try {
                return Long.parseLong(principal.getUsername());
            } catch (NumberFormatException e) {
                throw new RuntimeException("유효하지 않은 사용자 ID입니다: " + principal.getUsername());
            }
        }

        throw new RuntimeException("인증된 사용자 정보를 찾을 수 없습니다.");
    }

    /**
     * 현재 로그인한 사용자 이메일 조회 (필요시 사용)
     * @return 사용자 이메일
     */
    protected String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }

        throw new RuntimeException("인증된 사용자 정보를 찾을 수 없습니다.");
    }

    /**
     * 인증 여부 확인
     * @return 인증 여부
     */
    protected boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}