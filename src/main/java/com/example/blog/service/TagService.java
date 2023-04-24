package com.example.blog.service;

import com.example.blog.dto.TagDto;
import com.example.blog.entity.Tag;
import com.example.blog.exception.TagAlreadyExistsException;
import com.example.blog.exception.TagNotFoundException;
import com.example.blog.repository.PostRepository;
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
            return "Category '" + tag + "' does not exist";
        }
        return "Category '" + tag + "' does  exist";
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
            throw new TagNotFoundException("Category '" + tagName + "' not found!");
        }

        tagRepository.deleteByName(tagName);

        return "Tag '" + tagName + "' deleted!";
    }
}
