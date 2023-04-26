package com.example.blog.repository;

import com.example.blog.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().name("category").build();
        categoryRepository.save(category);
    }

    @Test
    void testExistsByNameShouldReturnTrue() {
        // When
        boolean expected = categoryRepository.existsByName(category.getName());

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByNameShouldReturnFalse() {
        // Given
        String nameToFail = "Fail";

        // When
        boolean expected = categoryRepository.existsByName(nameToFail);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindByNameShouldReturnCategory() {
        // When
        Category expected = categoryRepository.findByName(category.getName());

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeleteByNameShouldReturnVoid() {
        // Given
        categoryRepository.deleteByName(category.getName());

        // When
        Optional<Category> expected = categoryRepository.findById(category.getId());

        // Then
        assertThat(expected).isEmpty();
    }
}