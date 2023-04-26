package com.example.blog.repository;

import com.example.blog.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PostTagRepositoryTest {

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    private Post post;
    private Tag tag;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .firstName("John")
                .lastName("Evans")
                .email("john_evans@gmail.com")
                .password("Test.123")
                .repeatPassword("Test.123")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        post = Post.builder()
                .title("Test")
                .content("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();
        postRepository.save(post);

        tag = Tag.builder().name("tag").build();
        tagRepository.save(tag);

        PostTag postTag = PostTag.builder()
                .postTagId(new PostTagId(post.getId(), tag.getId()))
                .post(post)
                .tag(tag)
                .build();
        postTagRepository.save(postTag);
    }

    @Test
    void testExistsByPostAndTagShouldReturnTrue() {
        // When
        boolean expected = postTagRepository.existsByPostAndTag(post, tag);

        // Then
        assertThat(expected).isTrue();
    }

    @Test
    void testExistsByPostAndTagShouldReturnFalse() {
        // Given
        Tag tagToFail = Tag.builder().name("fail").build();
        tagRepository.save(tagToFail);

        // When
        boolean expected = postTagRepository.existsByPostAndTag(post, tagToFail);

        // Then
        assertThat(expected).isFalse();
    }

    @Test
    void testFindByPostAndTagShouldReturnPostTag() {
        // When
        PostTag expected = postTagRepository.findByPostAndTag(post, tag);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testFindByPostShouldReturnPostTagList() {
        // When
        List<PostTag> expected = postTagRepository.findByPost(post);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testFindByTagShouldReturnPostTagList() {
        // When
        List<PostTag> expected = postTagRepository.findByTag(tag);

        // Then
        assertThat(expected).isNotNull();
    }
}