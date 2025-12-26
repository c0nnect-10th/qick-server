package connect.qick.domain.volunteer.service;

import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VolunteerWorkService {
    private final VolunteerWorkRepository volunteerWorkRepository;

    public List<VolunteerWorkResponse> findAll() {
        return volunteerWorkRepository.findAllSummary();
    }
}
