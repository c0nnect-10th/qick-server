package connect.qick.domain.tcode.controller;

import connect.qick.domain.tcode.dto.request.CreateTCodeRequest;
import connect.qick.domain.tcode.dto.response.TCodeResponse;
import connect.qick.domain.tcode.service.TCodeService;
import connect.qick.global.data.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/t-codes")
@Tag(name = "TCode Management", description = "관리자용 TCode 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class TCodeAdminController {

    private final TCodeService tCodeService;

    @PostMapping
    @Operation(summary = "TCode 생성", description = "새로운 선생님 TCode를 생성합니다.")
    public ResponseEntity<ApiResponse<TCodeResponse>> createTCode(@RequestBody @Valid CreateTCodeRequest request) {
        TCodeResponse response = tCodeService.createTCode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    @Operation(summary = "모든 TCode 목록 조회", description = "생성된 모든 TCode 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<TCodeResponse>>> getAllTCodes() {
        return ResponseEntity.ok(ApiResponse.ok(tCodeService.getAllTCodes()));
    }

    @DeleteMapping("/{tCodeId}")
    @Operation(summary = "TCode 삭제", description = "특정 TCode를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteTCode(@PathVariable Long tCodeId) {
        tCodeService.deleteTCode(tCodeId);
        return ResponseEntity.ok(ApiResponse.ok("TCode가 성공적으로 삭제되었습니다."));
    }
}
