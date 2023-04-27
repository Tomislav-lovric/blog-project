package com.example.blog.service;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.dto.PostUpdateRequest;
import com.example.blog.entity.*;
import com.example.blog.exception.CategoryNotFoundException;
import com.example.blog.exception.PostNotFoundException;
import com.example.blog.exception.PostTitleAlreadyExistsException;
import com.example.blog.exception.TagNotFoundException;
import com.example.blog.repository.*;
import com.example.blog.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private static final String TOKEN = "bearer token";

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostCategoryRepository postCategoryRepository;

    @Mock
    private PostTagRepository postTagRepository;

    @InjectMocks
    private PostService postService;

    private Post post;
    private User user;
    private Category category;
    private Tag tag;
    private PostCategory postCategory;
    private PostTag postTag;
    private PostRequest postRequest;
    private PostUpdateRequest postUpdateRequest;

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

        category = Category.builder().name("category").build();

        tag = Tag.builder().name("tag").build();

        postCategory = PostCategory.builder()
                .postCategoryId(new PostCategoryId(post.getId(), category.getId()))
                .post(post)
                .category(category)
                .build();

        postTag = PostTag.builder()
                .postTagId(new PostTagId(post.getId(), tag.getId()))
                .post(post)
                .tag(tag)
                .build();

        Set<String> categories = new HashSet<>();
        categories.add("category");

        Set<String> tags = new HashSet<>();
        tags.add("tag");

        postRequest = PostRequest.builder()
                .title("Test")
                .content("test")
                .categories(categories)
                .tags(tags)
                .build();

//        postResponse = PostResponse.builder()
//                .title("Test")
//                .content("test")
//                .categories(categories.stream().toList())
//                .tags(tags.stream().toList())
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();

        postUpdateRequest = PostUpdateRequest.builder()
                .title("New Title")
                .content("New Content")
                .build();
    }

    @Test
    void testCreatePostOptionalTagPresentShouldReturnPostResponse() {
        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postRequest.getTitle(), user)).thenReturn(false);
        when(categoryRepository.existsByName(postRequest.getCategories().iterator().next())).thenReturn(true);
        when(categoryRepository.findByName(postRequest.getCategories().iterator().next())).thenReturn(category);
        when(tagRepository.findByName(postRequest.getTags().iterator().next())).thenReturn(Optional.of(tag));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse expected = postService.createPost(postRequest, TOKEN);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getTitle()).isEqualTo(post.getTitle());
        assertThat(expected.getContent()).isEqualTo(post.getContent());
        assertThat(expected.getCreatedAt().withNano(0)).isEqualTo(post.getCreatedAt());
        assertThat(expected.getUpdatedAt().withNano(0)).isEqualTo(post.getUpdatedAt());
    }

    @Test
    void testCreatePostOptionalTagNotPresentShouldReturnPostResponse() {
        // Given
        Tag newTag = new Tag();
        newTag.setName("New Tag");
        postRequest.getTags().clear();
        postRequest.getTags().add(newTag.getName());

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postRequest.getTitle(), user)).thenReturn(false);
        when(categoryRepository.existsByName(postRequest.getCategories().iterator().next())).thenReturn(true);
        when(categoryRepository.findByName(postRequest.getCategories().iterator().next())).thenReturn(category);
        when(tagRepository.findByName(newTag.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(newTag);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse expected = postService.createPost(postRequest, TOKEN);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getTitle()).isEqualTo(post.getTitle());
        assertThat(expected.getContent()).isEqualTo(post.getContent());
        assertThat(expected.getCreatedAt().withNano(0)).isEqualTo(post.getCreatedAt());
        assertThat(expected.getUpdatedAt().withNano(0)).isEqualTo(post.getUpdatedAt());
    }

    @Test
    void testCreatePostShouldThrowPostTitleAlreadyExistsException() {
        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postRequest.getTitle(), user)).thenReturn(true);

        // Then
        assertThatThrownBy(() -> postService.createPost(postRequest, TOKEN))
                .isInstanceOf(PostTitleAlreadyExistsException.class)
                .hasMessageContaining("Post with that title already exists, please use different title");
    }

    @Test
    void testCreatePostShouldThrowCategoryNotFoundException() {
        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postRequest.getTitle(), user)).thenReturn(false);
        when(categoryRepository.existsByName(postRequest.getCategories().iterator().next())).thenReturn(false);

        // Then
        assertThatThrownBy(() -> postService.createPost(postRequest, TOKEN))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining(
                        "Category by the name " + postRequest.getCategories().iterator().next() + " not found!"
                );
    }

    @Test
    void testGetPostShouldReturnPostResponse() {
        // Given
        String postTitle = "Title";

        // When
        when(postRepository.existsByTitle(postTitle)).thenReturn(true);
        when(postRepository.findByTitle(postTitle)).thenReturn(post);

        PostResponse expected = postService.getPost(postTitle);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testGetPostShouldThrowPostNotFoundException() {
        // Given
        String postTitle = "Title Not Found";

        // When
        when(postRepository.existsByTitle(postTitle)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> postService.getPost(postTitle))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postTitle + "' not found!");
    }

    @Test
    void getAllPosts() {
        // When
        when(postRepository.findAll()).thenReturn(List.of(post));

        List<PostResponse> expected = postService.getAllPosts();

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.size()).isEqualTo(1);
    }

    @Test
    void testUpdatePostTitleNotNullShouldReturnPostResponse() {
        // Given
        String postTitleToUpdate = "Title";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postTitleToUpdate, user)).thenReturn(true);
        when(postRepository.findByTitleAndUser(postTitleToUpdate, user)).thenReturn(post);
        when(postRepository.existsByTitleAndUser(postUpdateRequest.getTitle(), user)).thenReturn(false);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse expected = postService.updatePost(postTitleToUpdate, postUpdateRequest, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testUpdatePostTitleNullShouldReturnPostResponse() {
        // Given
        String postTitleToUpdate = "Title";
        postUpdateRequest.setTitle(null);

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postTitleToUpdate, user)).thenReturn(true);
        when(postRepository.findByTitleAndUser(postTitleToUpdate, user)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse expected = postService.updatePost(postTitleToUpdate, postUpdateRequest, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testUpdatePostShouldThrowPostNotFoundException() {
        // Given
        String postTitleToUpdate = "Title";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postTitleToUpdate, user)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> postService.updatePost(postTitleToUpdate, postUpdateRequest, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postTitleToUpdate + "' not found!");
    }

    @Test
    void testUpdatePostShouldThrowPostTitleAlreadyExistsException() {
        // Given
        String postTitleToUpdate = "Title";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postTitleToUpdate, user)).thenReturn(true);
        when(postRepository.findByTitleAndUser(postTitleToUpdate, user)).thenReturn(post);
        when(postRepository.existsByTitleAndUser(postUpdateRequest.getTitle(), user)).thenReturn(true);

        // Then
        assertThatThrownBy(() -> postService.updatePost(postTitleToUpdate, postUpdateRequest, TOKEN))
                .isInstanceOf(PostTitleAlreadyExistsException.class)
                .hasMessageContaining(
                        "Post with title '" +
                                postUpdateRequest.getTitle() +
                                "' already exists, please use different title"
                );
    }

    @Test
    void testDeletePostShouldReturnString() {
        // Given
        String postTitleToDelete = "Title";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postTitleToDelete, user)).thenReturn(true);
        doNothing().when(postRepository).deleteByTitleAndUser(postTitleToDelete, user);

        String expected = postService.deletePost(postTitleToDelete, TOKEN);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void testDeletePostShouldThrowPostNotFoundException() {
        // Given
        String postTitleToDelete = "Title";

        // When
        when(jwtService.extractUsername(any())).thenReturn(user.getEmail());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(postRepository.existsByTitleAndUser(postTitleToDelete, user)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> postService.deletePost(postTitleToDelete, TOKEN))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with the title '" + postTitleToDelete + "' not found!");
    }

    @Test
    void testGetAllPostsByCategoryShouldReturnPostResponseList() {
        // Given
        String categoryName = "category";

        // When
        when(categoryRepository.existsByName(categoryName)).thenReturn(true);
        when(categoryRepository.findByName(categoryName)).thenReturn(category);
        when(postCategoryRepository.findByCategory(category)).thenReturn(List.of(postCategory));

        List<PostResponse> expected = postService.getAllPostsByCategory(categoryName);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.get(0).getTitle()).isEqualTo(post.getTitle());
        assertThat(expected.get(0).getContent()).isEqualTo(post.getContent());
    }

    @Test
    void testGetAllPostsByCategoryShouldThrowCategoryNotFoundException() {
        // Given
        String categoryName = "category";

        // When
        when(categoryRepository.existsByName(categoryName)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> postService.getAllPostsByCategory(categoryName))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category by the name " + categoryName + " not found!");
    }

    @Test
    void testGetAllPostsByTagShouldReturnPostResponseList() {
        // Given
        String tagName = "tag";

        // When
        when(tagRepository.existsByName(tagName)).thenReturn(true);
        when(tagRepository.findTagByName(tagName)).thenReturn(tag);
        when(postTagRepository.findByTag(tag)).thenReturn(List.of(postTag));

        List<PostResponse> expected = postService.getAllPostsByTag(tagName);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.get(0).getTitle()).isEqualTo(post.getTitle());
        assertThat(expected.get(0).getContent()).isEqualTo(post.getContent());
    }

    @Test
    void testGetAllPostsByTagShouldThrowTagNotFoundException() {
        // Given
        String tagName = "tag";

        // When
        when(tagRepository.existsByName(tagName)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> postService.getAllPostsByTag(tagName))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("Tag by the name " + tagName + " not found!");
    }
}