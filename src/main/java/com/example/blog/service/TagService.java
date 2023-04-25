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

    @Transactional
    public String addTagToPost(String postTitle, TagDto request, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitleAndUser(postTitle, user)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        if (!tagRepository.existsByName(request.getName())) {
            Tag newTag = new Tag();
            newTag.setName(request.getName());
            tagRepository.save(newTag);
        }

        var post = postRepository.findByTitleAndUser(postTitle, user);
        var tag = tagRepository.findTagByName(request.getName());

        if (postTagRepository.existsByPostAndTag(post, tag)) {
            throw new PostAlreadyContainsThatTagException(
                    "Post '" + postTitle + "' already contains tag '" + request.getName() + "'"
            );
        }

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

    @Transactional
    public String deleteTagFromPost(String postTitle, TagDto request, String bearerToken) {
        String username = jwtService.extractUsername(bearerToken.substring(7));
        var user = userRepository.findUserByEmail(username);

        if (!postRepository.existsByTitleAndUser(postTitle, user)) {
            throw new PostNotFoundException("Post with the title '" + postTitle + "' not found!");
        }
        if (!tagRepository.existsByName(request.getName())) {
            throw new TagNotFoundException("Category '" + request.getName() + "' not found!");
        }

        var post = postRepository.findByTitleAndUser(postTitle, user);
        var tag = tagRepository.findTagByName(request.getName());

        if (!postTagRepository.existsByPostAndTag(post, tag)) {
            throw new PostDoesNotContainThatTagException(
                    "Post '" + postTitle + "' does not contain tag '" + request.getName() + "'"
            );
        }

        var postTags = post.getPostTags();
        var postTagToRemove = postTagRepository.findByPostAndTag(post, tag);

        postTags.remove(postTagToRemove);
        post.setPostTags(postTags);
        postRepository.save(post);

        return request.getName() + " tag removed from the post '" + postTitle + "'";
    }
}
