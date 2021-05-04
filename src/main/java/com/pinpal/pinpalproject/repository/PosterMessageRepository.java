package com.pinpal.pinpalproject.repository;

import com.pinpal.pinpalproject.model.PosterMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PosterMessageRepository extends JpaRepository<PosterMessage, Long> {

    Optional<List<PosterMessage>> findAllByPosterIdOrderByTimestampAsc(long posterId);
}
