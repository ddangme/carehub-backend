package carehub.web.dto.guardian;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 초대 처리 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
    private Long guardianId;
    private String status;
    private String message;

    public static InvitationResponse accepted(Long guardianId) {
        return InvitationResponse.builder()
                .guardianId(guardianId)
                .status("ACCEPTED")
                .message("초대를 수락했습니다.")
                .build();
    }

    public static InvitationResponse rejected(Long guardianId) {
        return InvitationResponse.builder()
                .guardianId(guardianId)
                .status("REJECTED")
                .message("초대를 거절했습니다.")
                .build();
    }
}
