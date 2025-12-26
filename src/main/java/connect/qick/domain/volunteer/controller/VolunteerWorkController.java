package connect.qick.domain.volunteer.controller;

import connect.qick.domain.volunteer.dto.request.CreateVolunteerWorkRequest;
import connect.qick.domain.volunteer.dto.response.CreateVolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse;
import connect.qick.domain.volunteer.service.VolunteerWorkService;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.security.entity.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("volunteer")
@Tag(name = "Volunteer", description = "심부름 조회 및 관리 API")
public class VolunteerWorkController {

    private final VolunteerWorkService volunteerWorkService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<VolunteerWorkSummaryResponse>>> getVolunteerWorks() {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        volunteerWorkService.findAll()
                )
        );
    }

    @GetMapping("/{workId}")
    public ResponseEntity<ApiResponse<VolunteerWorkResponse>> getVolunteerWork(@PathVariable Long workId) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        volunteerWorkService.findVolunteerWork(workId)
                )
        );
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateVolunteerWorkResponse>> createVolunteerWork(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateVolunteerWorkRequest request
            ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        volunteerWorkService.create(
                                request.name(),
                                request.maxParticipants(),
                                request.location(),
                                request.description(),
                                request.difficulty(),
                                request.start_time(),
                                userDetails.getGoogleId())
                )
        );
    }

    @DeleteMapping("/delete/{workId}")
    public ResponseEntity<ApiResponse<String>> deleteVolunteerWork(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long workId
    ) {
        volunteerWorkService.deleteVolunteerWork(workId, userDetails.getGoogleId());

        return ResponseEntity.ok(
                ApiResponse.ok("봉사활동이 정상적으로 삭제되었습니다.")
        );
    }
}
