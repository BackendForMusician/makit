package com.example.makit.feedUpload.Controller;

import com.example.makit.feedUpload.Entity.TagEntity;
import com.example.makit.feedUpload.Repository.TagRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {
    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public List<TagEntity> getAllTags() {
        return tagRepository.findAll();
    }
}
