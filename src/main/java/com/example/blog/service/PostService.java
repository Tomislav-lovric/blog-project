package com.example.blog.service;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.entity.*;
import com.example.blog.exception.CategoryNotFoundException;
import com.example.blog.exception.PostNotFoundException;
import com.example.blog.exception.PostTitleAlreadyExistsException;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.TagRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final JwtService jwtService;

    //Quite few things are happening in this method therefore there may be more comments
    //than usual just to explain what is happening (or should I say what I was thinking
    //when writing the code)
    public PostResponse createPost(PostRequest request, String bearerToken) {
        // username is users email in our case
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (postRepository.existsByTitleAndUser(request.getTitle(), user)) {
            throw new PostTitleAlreadyExistsException("Post with that title already exists, please use different title");
        }

        //First we build the post, without categories and tags because user may not
        //want to add them immediately, also we build post so we can use it and
        //its id later in the code when we need it
        var post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        //Since we are using for both categories and tags in our PostRequest dto Set so values are unique
        //(because we do not want to allow users to have duplicate categories/tags on their posts)
        //we have to add those elements to a List, so we can add it to our post later
        List<PostCategory> postCategories = new ArrayList<>();
        for (String category : request.getCategories()) {
            if (!categoryRepository.existsByName(category)) {
                throw new CategoryNotFoundException("Category by the name" + category + " not found!");
            }

            var categoryToAdd = categoryRepository.findByName(category);
            //Since we are using bridge table to connect our posts and categories tables,
            //and therefore we can't directly add categories to our post, we have to build a var of
            //our bridge table
            var postCategory = PostCategory.builder()
                    .postCategoryId(new PostCategoryId(post.getId(), categoryToAdd.getId()))
                    .post(post)
                    .category(categoryToAdd)
                    .build();

            //and then add that var to our previously created list
            postCategories.add(postCategory);
        }
        //and finally add the list to our post
        post.setPostCategories(postCategories);

        //The same thing we are doing for categories, we are doing for tags as well
        //with one additional thing. Unlike categories, tags are more arbitrary therefore
        //we want to allow users to create their own tags, so even if tag provided by user
        //does not exist in our database the code will run, because we will simply create
        //the tag that, user provided, that does not exist and save it to our database
        List<PostTag> postTagLIst = new ArrayList<>();
        for (String tag : request.getTags()) {
            Optional<Tag> optionalTag = tagRepository.findByName(tag);
            if (optionalTag.isPresent()) {
                var postTag = PostTag.builder()
                        .postTagId(new PostTagId(post.getId(), optionalTag.get().getId()))
                        .post(post)
                        .tag(optionalTag.get())
                        .build();
                postTagLIst.add(postTag);
            } else {
                Tag newTag = new Tag();
                newTag.setName(tag);
                var postTag = PostTag.builder()
                        .postTagId(new PostTagId(post.getId(), newTag.getId()))
                        .post(post)
                        .tag(tagRepository.save(newTag))
                        .build();
                postTagLIst.add(postTag);
            }
        }
        post.setPostTags(postTagLIst);
        postRepository.save(post);

        return PostResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public PostResponse getPost(String postTitle, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitleAndUser(postTitle, user)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }

        var post = postRepository.findByTitleAndUser(postTitle, user);

        return PostResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    @Transactional
    public String deletePost(String postTitle, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitleAndUser(postTitle, user)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }

        postRepository.deleteByTitleAndUser(postTitle, user);

        return "Post with the title '" + postTitle + "' deleted";
    }

}
