package com.example.blog.repository;

import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("John")
                .lastName("Evans")
                .email("john_evans@gmail.com")
                .password("Test.123")
                .repeatPassword("Test.123")
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    @Test
    void testFindByEmailShouldReturnOptionalUser() {
        // When
        User expected = userRepository.findByEmail(user.getEmail()).get();

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testExistsByEmailShouldReturnTrue() {
        // When
        boolean expected = userRepository.existsByEmail(user.getEmail());

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByEmailShouldReturnFalse() {
        // Given
        String emailToFail = "email@email.com";

        // When
        boolean expected = userRepository.existsByEmail(emailToFail);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindUserByEmailShouldReturnUser() {
        // When
        User expected = userRepository.findUserByEmail(user.getEmail());

        // Then
        assertThat(expected).isNotNull();
    }
}