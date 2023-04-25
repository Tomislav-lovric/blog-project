package com.example.blog.service;

import com.example.blog.dto.TagDto;
import com.example.blog.entity.PostTag;
import com.example.blog.entity.PostTagId;
import com.example.blog.entity.Tag;
import com.example.blog.exception.*;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.PostTagRepository;
import com.example.blog.repository.TagRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PostTagRepository postTagRepository;

    //Just like in CategoryService most of these methods are simple methods for CRUD operations so
    //there is no need to explain what they do, especially since they are not using
    //any difficult logic only last two methods will be explained somewhat
    public TagDto createTag(TagDto request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new TagAlreadyExistsException("Tag '" + request.getName() + "' already exists!");
        }

        var tag = Tag.builder().name(request.getName()).build();
        tagRepository.save(tag);

        return new TagDto(request.getName());
    }

    public String searchTag(String tag) {
        if (!tagRepository.existsByName(tag)) {
            return "Tag '" + tag + "' does not exist";
        }
        return "Tag '" + tag + "' does  exist";
    }

    public List<TagDto> getAllTags() {
        List<Tag> tags = tagRepository.findAll();

        return tags.stream().map(tag -> new TagDto(tag.getName())).toList();
    }

    @Transactional
    public String updateTag(String tagName, TagDto request) {
        if (!tagRepository.existsByName(tagName)) {
            throw new TagNotFoundException("Category '" + tagName + "' not found!");
        }
        if (tagRepository.existsByName(request.getName())) {
            throw new TagAlreadyExistsException("Tag '" + request.getName() + "' already exists!");
        }

        var tag = tagRepository.findTagByName(tagName);
        tag.setName(request.getName());


        return "Tag '" + tagName + "' changed/updated to '" + request.getName() + "'";
    }

    @Transactional
    public String deleteTag(String tagName) {
        if (!tagRepository.existsByName(tagName)) {
            throw new TagNotFoundException("Tag '" + tagName + "' not found!");
        }

        tagRepository.deleteByName(tagName);

        return "Tag '" + tagName + "' deleted!";
    }

    //Again just like in CategoryService, unlike previous methods here we need to use bridge table
    //which makes this method look somewhat more complicated (which it isn't).
    //However, unlike in CategoryService this method is little bit different
    @Transactional
    public String addTagToPost(String postTitle, TagDto request, String bearerToken) {
        //Again, unlike previous methods here we are editing posts and only users that created that post
        //can edit it, that is why we are fetching user by getting token from header and
        //extracting username (email in our case) from it and using it to get user
        //(we do not need to check if user exists in our db since if he does not exist
        //the token will fail)
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitleAndUser(postTitle, user)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        //Here is where this method differs from the one from CategoryService.
        //Unlike in CategoryService, here if the tag the user provided does not exist
        //we create it and save it to our db. We do that because unlike categories
        //tags are more arbitrary
        if (!tagRepository.existsByName(request.getName())) {
            Tag newTag = new Tag();
            newTag.setName(request.getName());
            tagRepository.save(newTag);
        }

        //And just like already said the rest of the code is pretty much the same as CategoryService
        //After we did the check we get post and tag
        var post = postRepository.findByTitleAndUser(postTitle, user);
        var tag = tagRepository.findTagByName(request.getName());

        //Which we then use to check if they are connected. If they are not throw exception, else continue
        if (postTagRepository.existsByPostAndTag(post, tag)) {
            throw new PostAlreadyContainsThatTagException(
                    "Post '" + postTitle + "' already contains tag '" + request.getName() + "'"
            );
        }

        //If it passes we get PostTag from our Post to add the new PostTag, and save our post
        // (basically we are connecting our post with the tag using bridge table)
        var postTags = post.getPostTags();
        var postTagToAdd = PostTag.builder()
                .postTagId(new PostTagId(post.getId(), tag.getId()))
                .post(post)
                .tag(tag)
                .build();

        postTags.add(postTagToAdd);
        post.setPostTags(postTags);
        postRepository.save(post);

        return request.getName() + " tag added to the post '" + postTitle + "'";
    }

    //This one is very similar to the method before it, with just couple of changes
    @Transactional
    public String deleteTagFromPost(String postTitle, TagDto request, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitleAndUser(postTitle, user)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        //Here unlike before, if tag does not exist in our db we throw exception.
        //We do that because if tag does not exist that means the post can not be connected to it
        if (!tagRepository.existsByName(request.getName())) {
            throw new TagNotFoundException("Category '" + request.getName() + "' not found!");
        }

        var post = postRepository.findByTitleAndUser(postTitle, user);
        var tag = tagRepository.findTagByName(request.getName());

        //Unlike in previous method, here we check if connection does not exist (because we need it to exist
        //to be able to remove/delete it), if it does not exist throw exception
        if (!postTagRepository.existsByPostAndTag(post, tag)) {
            throw new PostDoesNotContainThatTagException(
                    "Post '" + postTitle + "' does not contain tag '" + request.getName() + "'"
            );
        }

        //Then just like before we get our PostTag from our post, however unlike before we do not
        //add the new connection to our bridge table but rather remove the tag from our post (PostTag)
        //and then save that updated post again to our db
        var postTags = post.getPostTags();
        var postTagToRemove = postTagRepository.findByPostAndTag(post, tag);

        postTags.remove(postTagToRemove);
        post.setPostTags(postTags);
        postRepository.save(post);

        return request.getName() + " tag removed from the post '" + postTitle + "'";
    }
}
