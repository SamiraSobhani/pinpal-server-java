package com.pinpal.pinpalproject.controller;

import com.pinpal.pinpalproject.exception.BadRequestException;
import com.pinpal.pinpalproject.model.AuthProvider;
import com.pinpal.pinpalproject.model.ReCaptchaResponse;
import com.pinpal.pinpalproject.model.User;
import com.pinpal.pinpalproject.payload.ApiResponse;
import com.pinpal.pinpalproject.payload.AuthResponse;
import com.pinpal.pinpalproject.payload.LoginRequest;
import com.pinpal.pinpalproject.payload.SignUpRequest;
import com.pinpal.pinpalproject.repository.UserRepository;
import com.pinpal.pinpalproject.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String RECAPTCHA_SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String SECRET_KEY = "ENTER_HERE_YOUR_SECRET_KEY";

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final RestTemplate restTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        if (isTokenValid(request.getHeader("recaptchaToken"))) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProvider.createToken(authentication);
            return ResponseEntity.ok(new AuthResponse(token));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
//        if (isTokenValid(request.getHeader("recaptchaToken"))) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }
        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
//        }
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    private boolean isTokenValid(String recaptchaToken) {
        try {
            System.out.println("TOKEN IS " + recaptchaToken);

            MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
            requestMap.add("secret", "6Lf7NLQaAAAAAIpMyFu6lJK5iBmYlSEGohrxbSsZ");
            requestMap.add("response", recaptchaToken);
            ReCaptchaResponse response = restTemplate.postForObject("https://www.google.com/recaptcha/api/siteverify",
                    requestMap, ReCaptchaResponse.class);
            System.out.println(response.toString());
            System.out.println(response.getErrorCodes());
            if (response.getSuccess()) {
                if (response.getScore() >= 0.1) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.printf(e.getMessage());
        }
        return false;
    }
}
