package connect.qick.domain.volunteer.controller;

import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.service.VolunteerWorkService;
import connect.qick.global.data.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("volunteer")
@Tag(name = "Volunteer", description = "심부름 조회 및 관리 API")
public class VolunteerWorkController {

    private final VolunteerWorkService volunteerWorkService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<VolunteerWorkResponse>>> getVolunteerWorks() {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        volunteerWorkService.findAll()
                )
        );
    }
}
