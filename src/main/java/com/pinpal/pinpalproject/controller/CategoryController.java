package com.pinpal.pinpalproject.controller;

import com.pinpal.pinpalproject.model.Category;
import com.pinpal.pinpalproject.model.Poster;
import com.pinpal.pinpalproject.model.PosterMessage;
import com.pinpal.pinpalproject.model.User;
import com.pinpal.pinpalproject.payload.MessageRequest;
import com.pinpal.pinpalproject.repository.CategoryRepository;
import com.pinpal.pinpalproject.repository.PosterMessageRepository;
import com.pinpal.pinpalproject.repository.PosterRepository;
import com.pinpal.pinpalproject.repository.UserRepository;
import com.pinpal.pinpalproject.security.UserPrincipal;
import com.pinpal.pinpalproject.service.EmailServiceImpl;
import com.pinpal.pinpalproject.service.PosterMessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;


    @GetMapping("/categories")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categoryList = categoryRepository.findAll();
        return ResponseEntity.ok(categoryList);
    }



}
