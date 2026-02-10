package com.example.bibecoding01.position;

import com.example.bibecoding01._core.errors.BadRequestException;
import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01._core.errors.NotFoundException;
import com.example.bibecoding01.auth.SessionUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    public List<PositionResponse.OptionDTO> findOptions(Long selectedId) {
        return positionRepository.findAll().stream()
                .map(p -> new PositionResponse.OptionDTO(p.getId(), p.getName(), p.getId().equals(selectedId)))
                .toList();
    }

    public Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("직급을 찾을 수 없습니다."));
    }

    @Transactional
    public Long save(PositionRequest.SaveDTO request, SessionUser sessionUser) {
        requireAdmin(sessionUser);
        String name = request.getName().trim();

        positionRepository.findByName(name)
                .ifPresent(existing -> {
                    throw new BadRequestException("이미 존재하는 직급명입니다.");
                });

        Position position = new Position(name);
        return positionRepository.save(position).getId();
    }

    private void requireAdmin(SessionUser sessionUser) {
        if (sessionUser == null || !sessionUser.isAdmin()) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
    }
}
