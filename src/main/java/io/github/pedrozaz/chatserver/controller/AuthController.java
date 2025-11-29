package io.github.pedrozaz.chatserver.controller;

import io.github.pedrozaz.chatserver.domain.User;
import io.github.pedrozaz.chatserver.dto.AuthRequest;
import io.github.pedrozaz.chatserver.dto.AuthResponse;
import io.github.pedrozaz.chatserver.repository.MongoRepository;
import io.github.pedrozaz.chatserver.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final MongoRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest authRequest) {
        if (userRepository.findByUsername(authRequest.username()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User user = User.builder()
                .username(authRequest.username())
                .password(passwordEncoder.encode(authRequest.password()))
                .role("USER")
                .build();
        userRepository.save(user);

        String token = tokenProvider.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
        );
        String token = tokenProvider.generateToken(authRequest.username());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
