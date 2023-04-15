package com.example.blog.service;

import com.example.blog.dto.AuthenticationResponse;
import com.example.blog.dto.UserLoginRequest;
import com.example.blog.dto.UserRegisterRequest;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.exception.InvalidRepeatedPasswordException;
import com.example.blog.exception.UserAlreadyExistsException;
import com.example.blog.exception.UserNotFoundException;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with provided email already exists!");
        }
        if (!request.getPassword().equals(request.getRepeatPassword())) {
            throw new InvalidRepeatedPasswordException("Passwords do not match, please try again!");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .repeatPassword(passwordEncoder.encode(request.getRepeatPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse login(UserLoginRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new UserNotFoundException("User with provided email does not exist!");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findUserByEmail(request.getEmail());
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
