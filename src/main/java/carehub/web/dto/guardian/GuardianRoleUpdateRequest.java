package carehub.web.dto.guardian;

import carehub.domain.guardian.GuardianRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

// 보호자 권한 수정 요청 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardianRoleUpdateRequest {
    @NotNull
    private GuardianRole newRole;
}
