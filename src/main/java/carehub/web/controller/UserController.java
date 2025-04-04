package carehub.web.controller;

import carehub.common.dto.response.ApiResponse;
import carehub.domain.user.UserService;
import carehub.web.dto.auth.request.RegisterUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * @param request 회원가입 요청 객체
     * @return API 응답
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Long>> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {
        Long userId = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(userId));
    }
}
