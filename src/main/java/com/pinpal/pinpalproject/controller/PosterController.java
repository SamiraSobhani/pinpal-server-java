package com.pinpal.pinpalproject.controller;

import com.pinpal.pinpalproject.model.Poster;
import com.pinpal.pinpalproject.model.User;
import com.pinpal.pinpalproject.repository.CategoryRepository;
import com.pinpal.pinpalproject.repository.PosterRepository;
import com.pinpal.pinpalproject.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class PosterController {

    private final PosterRepository posterRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/posters/all")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<List<Poster>> getAllPosters(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Optional<List<Poster>> optionalPosterList = posterRepository.findAllByIsActiveIsTrue();
        return optionalPosterList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/posters/me")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<List<Poster>> getMyPosters(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Optional<List<Poster>> optionalPosterList = posterRepository.findAllByIsActiveIsTrueAndUserId(userPrincipal.getId());
        return optionalPosterList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/posters/applied")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<List<Poster>> getPostersApplied(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Optional<List<Poster>> optionalResultList = Optional.empty();
        Optional<List<Poster>> optionalPosterList = posterRepository.findAllByIsActiveIsTrue();
        if (optionalPosterList.isPresent()) {
            List<Poster> result = new ArrayList<>();
            List<Poster> posters = optionalPosterList.get();
            posters.forEach(poster -> {
                Set<User> applicants = poster.getApplicants();
                for (User user : applicants) {
                    if (user.getId().equals(userPrincipal.getId())) {
                        result.add(poster);
                        break;
                    }
                }
            });
            if (!result.isEmpty()) {
                optionalResultList = Optional.of(result);
            }
        }
        return optionalResultList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/poster")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity deletePoster(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                       @RequestParam Long id) {
        Optional<List<Poster>> optionalPosterList = posterRepository.findAllByIsActiveIsTrueAndUserId(userPrincipal.getId());
        if (optionalPosterList.isPresent()) {
            List<Poster> posters = optionalPosterList.get();
            for (Poster poster : posters) {
                if (poster.getId().equals(id)) {
                    posterRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/poster")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<Poster> addPoster(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @RequestBody Poster poster) {
        poster.setUser(User.from(userPrincipal));
        poster.setCategory(categoryRepository.findById(poster.getCategory().getId()).get());
        try {
            Poster createdPoster = posterRepository.save(poster);
            return ResponseEntity.ok(createdPoster);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping("/poster")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<Poster> getPoster(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long id) {
        Optional<Poster> posterOptional = posterRepository.findById(id);
        return posterOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
