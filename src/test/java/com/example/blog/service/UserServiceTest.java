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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRegisterRequest registerRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("John")
                .lastName("Evans")
                .email("john_evans@gmail.com")
                .password(passwordEncoder.encode("Test.123"))
                .repeatPassword(passwordEncoder.encode("Test.123"))
                .role(Role.USER)
                .build();

        registerRequest = UserRegisterRequest.builder()
                .firstName("John")
                .lastName("Evans")
                .email("john_evans@gmail.com")
                .password("Test.123")
                .repeatPassword("Test.123")
                .build();

        loginRequest = UserLoginRequest.builder()
                .email("john_evans@gmail.com")
                .password("Test.123")
                .build();
    }

    @Test
    void testRegisterShouldReturnAuthenticationResponse() {
        // Given
        String token = "token";

        // When
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(token);

        AuthenticationResponse response = userService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(token);
    }

    @Test
    void testRegisterShouldThrowUserAlreadyExistsException() {
        // When
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with provided email already exists!");
    }

    @Test
    void testRegisterShouldThrowInvalidRepeatedPasswordException() {
        // given
        String missMatchPassword = "Pass.123";
        registerRequest.setRepeatPassword(missMatchPassword);

        // when
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);

        // then
        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(InvalidRepeatedPasswordException.class)
                .hasMessageContaining("Passwords do not match, please try again!");
    }

    @Test
    void testLoginShouldReturnAuthenticationResponse() {
        // Given
        String token = "token";
        Authentication authentication = mock(Authentication.class);

        // When
        when(userRepository.existsByEmail(loginRequest.getEmail())).thenReturn(true);
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()))
        ).thenReturn(authentication);
        when(userRepository.findUserByEmail(registerRequest.getEmail())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(token);

        AuthenticationResponse response = userService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(token);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
    }

    @Test
    void testLoginShouldThrowUserNotFoundException() {
        // When
        when(userRepository.existsByEmail(loginRequest.getEmail())).thenReturn(false);

        // Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with provided email does not exist!");
    }
}