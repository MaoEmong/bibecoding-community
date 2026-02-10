package com.example.bibecoding01.position;

import java.util.List;
import java.util.Optional;

public interface PositionRepository {

    Position save(Position position);

    Optional<Position> findById(Long id);

    Optional<Position> findByName(String name);

    List<Position> findAll();
}
