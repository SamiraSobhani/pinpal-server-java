package com.pinpal.pinpalproject.repository;

import com.pinpal.pinpalproject.model.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PosterRepository extends JpaRepository<Poster, Long> {

    Optional<List<Poster>> findAllByIsActiveIsTrue();

    Optional<List<Poster>> findAllByIsActiveIsTrueAndUserId(Long id);
}