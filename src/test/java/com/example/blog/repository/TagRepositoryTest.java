package com.example.blog.repository;

import com.example.blog.entity.Tag;
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
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = Tag.builder().name("tag").build();
        tagRepository.save(tag);
    }

    @Test
    void testExistsByNameShouldReturnTrue() {
        // When
        boolean expected = tagRepository.existsByName(tag.getName());

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByNameShouldReturnFalse() {
        // Given
        String nameToFail = "Fail";

        // When
        boolean expected = tagRepository.existsByName(nameToFail);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindByNameShouldReturnOptionalTag() {
        // When
        Optional<Tag> expected = tagRepository.findByName(tag.getName());

        // Then
        assertThat(expected).isPresent();
    }

    @Test
    void testFindTagByNameShouldReturnTag() {
        // When
        Tag expected = tagRepository.findTagByName(tag.getName());

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeleteByNameShouldReturnVoid() {
        // Given
        tagRepository.deleteByName(tag.getName());

        // When
        Optional<Tag> expected = tagRepository.findById(tag.getId());

        // Then
        assertThat(expected).isEmpty();
    }
}