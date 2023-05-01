package com.example.blog.service;

import com.example.blog.dto.TagDto;
import com.example.blog.entity.*;
import com.example.blog.exception.*;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.PostTagRepository;
import com.example.blog.repository.TagRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    private static final String TOKEN = "bearer token";

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PostTagRepository postTagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag;
    private TagDto tagDto;
    private Post post;
    private User user;
    private PostTag postTag;

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

        post = Post.builder()
                .title("Test")
                .content("test")
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .user(user)
                .build();

        tag = Tag.builder().name("tag").build();

        tagDto = TagDto.builder().name("tag").build();

        postTag = PostTag.builder()
                .postTagId(new PostTagId(post.getId(), tag.getId()))
                .post(post)
                .tag(tag)
                .build();
    }

    @Test
    void testCreateTagShouldReturnTagDto() {
        // When
        when(tagRepository.existsByName(tagDto.getName())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        TagDto expected = tagService.createTag(tagDto);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getName()).isEqualTo(tag.getName());
    }

    @Test
    void testCreateTagShouldThrowTagAlreadyExistsException() {
        // When
        when(tagRepository.existsByName(tagDto.getName())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> tagService.createTag(tagDto))
                .isInstanceOf(TagAlreadyExistsException.class)
                .hasMessageContaining("Tag '" + tagDto.getName() + "' already exists!");
    }

    @Test
    void testSearchTagTagExistsShouldReturnString() {
        // Given
        String tagToFind = "tag";

        // When
        when(tagRepository.existsByName(tagToFind)).thenReturn(true);

        String expected = tagService.searchTag(tagToFind);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testSearchTagTagDoesNotExistShouldReturnString() {
        // Given
        String tagToFind = "tag";

        // When
        when(tagRepository.existsByName(tagToFind)).thenReturn(false);

        String expected = tagService.searchTag(tagToFind);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testGetAllTagsShouldReturnTagDtoList() {
        // When
        when(tagRepository.findAll()).thenReturn(List.of(tag));

        List<TagDto> expected = tagService.getAllTags();

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.get(0).getName()).isEqualTo(tag.getName());
    }

    @Test
    void testUpdateTagShouldReturnString() {
        // Given
        String tagToUpdate = "tag";

        // When
        when(tagRepository.existsByName(anyString())).thenReturn(true, false);
        when(tagRepository.findTagByName(tagToUpdate)).thenReturn(tag);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        String expected = tagService.updateTag(tagToUpdate, tagDto);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testUpdateTagShouldThrowTagNotFoundException() {
        // Given
        String tagToUpdate = "tag";

        // When
        when(tagRepository.existsByName(tagToUpdate)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> tagService.updateTag(tagToUpdate, tagDto))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("Category '" + tagToUpdate + "' not found!");
    }

    @Test
    void testUpdateTagShouldThrowTagAlreadyExistsException() {
        // Given
        String tagToUpdate = "tag";

        // When
        when(tagRepository.existsByName(anyString())).thenReturn(true, true);

        // Then
        assertThatThrownBy(() -> tagService.updateTag(tagToUpdate, tagDto))
                .isInstanceOf(TagAlreadyExistsException.class)
                .hasMessageContaining("Tag '" + tagDto.getName() + "' already exists!");
    }

    @Test
    void testDeleteTagShouldReturnString() {
        // Given
        String tagToDelete = "tag";

        // When
        when(tagRepository.existsByName(tagToDelete)).thenReturn(true);
        doNothing().when(tagRepository).deleteByName(tagToDelete);

        String expected = tagService.deleteTag(tagToDelete);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeleteTagShouldThrowTagNotFoundException() {
        // Given
        String tagToDelete = "tag";

        // When
        when(tagRepository.existsByName(tagToDelete)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> tagService.deleteTag(tagToDelete))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("Tag '" + tagToDelete + "' not found!");
    }

    @Test
    void testAddTagToPostTagExistsShouldReturnString() {
        // Given
        String postToAddTag = "Test";
        post.setPostTags(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToAddTag, user)).thenReturn(true);
        when(tagRepository.existsByName(tagDto.getName())).thenReturn(true);
        when(postRepository.findByTitleAndUser(postToAddTag, user)).thenReturn(post);
        when(tagRepository.findTagByName(tagDto.getName())).thenReturn(tag);
        when(postTagRepository.existsByPostAndTag(post, tag)).thenReturn(false);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        String expected = tagService.addTagToPost(postToAddTag, tagDto, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testAddTagToPostTagDoesNotExistShouldReturnString() {
        // Given
        String postToAddTag = "Test";
        post.setPostTags(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToAddTag, user)).thenReturn(true);
        when(tagRepository.existsByName(tagDto.getName())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(postRepository.findByTitleAndUser(postToAddTag, user)).thenReturn(post);
        when(tagRepository.findTagByName(tagDto.getName())).thenReturn(tag);
        when(postTagRepository.existsByPostAndTag(post, tag)).thenReturn(false);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        String expected = tagService.addTagToPost(postToAddTag, tagDto, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testAddTagToPostShouldThrowPostNotFoundException() {
        // Given
        String postToAddTag = "Test";
        post.setPostTags(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToAddTag, user)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> tagService.addTagToPost(postToAddTag, tagDto, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postToAddTag + "' not found!");
    }

    @Test
    void testAddTagToPostShouldThrowPostAlreadyContainsThatTagException() {
        // Given
        String postToAddTag = "Test";
        post.setPostTags(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToAddTag, user)).thenReturn(true);
        when(tagRepository.existsByName(tagDto.getName())).thenReturn(true);
        when(postRepository.findByTitleAndUser(postToAddTag, user)).thenReturn(post);
        when(tagRepository.findTagByName(tagDto.getName())).thenReturn(tag);
        when(postTagRepository.existsByPostAndTag(post, tag)).thenReturn(true);

        // Then
        assertThatThrownBy(() -> tagService.addTagToPost(postToAddTag, tagDto, TOKEN))
                .isInstanceOf(PostAlreadyContainsThatTagException.class)
                .hasMessageContaining(
                        "Post '" + postToAddTag + "' already contains tag '" + tagDto.getName() + "'"
                );
    }

    @Test
    void testDeleteTagFromPostShouldReturnString() {
        // Given
        String postToDeleteTag = "Test";
        post.setPostTags(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToDeleteTag, user)).thenReturn(true);
        when(tagRepository.existsByName(tagDto.getName())).thenReturn(true);
        when(postRepository.findByTitleAndUser(postToDeleteTag, user)).thenReturn(post);
        when(tagRepository.findTagByName(tagDto.getName())).thenReturn(tag);
        when(postTagRepository.existsByPostAndTag(post, tag)).thenReturn(true);
        when(postTagRepository.findByPostAndTag(post, tag)).thenReturn(postTag);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        String expected = tagService.deleteTagFromPost(postToDeleteTag, tagDto, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeleteTagFromPostShouldThrowPostNotFoundException() {
        // Given
        String postToDeleteTag = "Test";
        post.setPostTags(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToDeleteTag, user)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> tagService.deleteTagFromPost(postToDeleteTag, tagDto, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postToDeleteTag + "' not found!");
    }

    @Test
    void testDeleteTagFromPostShouldThrowTagNotFoundException() {
        // Given
        String postToDeleteTag = "Test";
        post.setPostTags(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToDeleteTag, user)).thenReturn(true);
        when(tagRepository.existsByName(tagDto.getName())).thenReturn(false);

        // Then
        assertThatThrownBy(() -> tagService.deleteTagFromPost(postToDeleteTag, tagDto, TOKEN))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("Category '" + tagDto.getName() + "' not found!");
    }

    @Test
    void testDeleteTagFromPostShouldThrowPostDoesNotContainThatTagException() {
        // Given
        String postToDeleteTag = "Test";
        post.setPostTags(new ArrayList<>());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postToDeleteTag, user)).thenReturn(true);
        when(tagRepository.existsByName(tagDto.getName())).thenReturn(true);
        when(postRepository.findByTitleAndUser(postToDeleteTag, user)).thenReturn(post);
        when(tagRepository.findTagByName(tagDto.getName())).thenReturn(tag);
        when(postTagRepository.existsByPostAndTag(post, tag)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> tagService.deleteTagFromPost(postToDeleteTag, tagDto, TOKEN))
                .isInstanceOf(PostDoesNotContainThatTagException.class)
                .hasMessageContaining(
                        "Post '" + postToDeleteTag + "' does not contain tag '" + tagDto.getName() + "'"
                );
    }
}