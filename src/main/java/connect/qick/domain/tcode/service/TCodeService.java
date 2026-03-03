package connect.qick.domain.tcode.service;

import connect.qick.domain.tcode.dto.request.CreateTCodeRequest;
import connect.qick.domain.tcode.dto.response.TCodeResponse;
import connect.qick.domain.tcode.entity.TCodeEntity;
import connect.qick.domain.tcode.exception.TCodeException;
import connect.qick.domain.tcode.exception.TCodeStatusCode;
import connect.qick.domain.tcode.repository.TCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TCodeService {
    private static final int CODE_LENGTH = 5;
    private static final int MAX_GENERATION_ATTEMPTS = 20;

    private final TCodeRepository tCodeRepository;

    @Transactional
    public void verifyTCode(String code, String teacherName) {
        String normalizedCode = code == null ? null : code.trim();
        String normalizedTeacherName = teacherName == null ? null : teacherName.trim();

        TCodeEntity tCodeEntity = tCodeRepository.findByCodeForUpdate(normalizedCode)
                .orElseThrow(() -> new TCodeException(TCodeStatusCode.TCODE_NOT_FOUND));

        if (tCodeEntity.isUsed()) {
            throw new TCodeException(TCodeStatusCode.TCODE_ALREADY_USED);
        }

        if (!tCodeEntity.getTeacherName().equals(normalizedTeacherName)) {
            throw new TCodeException(TCodeStatusCode.TCODE_NAME_MISMATCH);
        }

        tCodeEntity.use();
    }

    @Transactional
    public TCodeResponse createTCode(CreateTCodeRequest request) {
        String newCode = generateUniqueFiveDigitCode();
        TCodeEntity tCode = TCodeEntity.builder()
                .code(newCode)
                .teacherName(request.getTeacherName().trim())
                .build();
        TCodeEntity savedTCode = tCodeRepository.save(tCode);
        return TCodeResponse.from(savedTCode);
    }

    public List<TCodeResponse> getAllTCodes() {
        return tCodeRepository.findAll().stream()
                .map(TCodeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTCode(Long tCodeId) {
        if (!tCodeRepository.existsById(tCodeId)) {
            throw new TCodeException(TCodeStatusCode.TCODE_NOT_FOUND);
        }
        tCodeRepository.deleteById(tCodeId);
    }

    private String generateUniqueFiveDigitCode() {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String code = String.format("%0" + CODE_LENGTH + "d", ThreadLocalRandom.current().nextInt(100000));
            if (!tCodeRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("TCode 생성에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }
}
