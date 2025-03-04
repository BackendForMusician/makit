package com.example.makit.feedUpload.Service;

import com.example.makit.feedUpload.Entity.TagEntity;
import com.example.makit.feedUpload.Repository.TagRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @PostConstruct
    public void initTags() {
        List<String> predefinedTags = Arrays.asList(
                "bouncy", "dark", "energetic", "soulful", "inspiring", "confident", "sad", "calm", "angry",
                "happy", "relaxed", "epic", "determined", "crazy", "intense", "loved", "dirty",
                "depressed", "lonely", "hyper", "evil", "peaceful", "grateful", "gloomy", "anxious",
                "powerful", "adored", "scary", "enraged", "lazy", "romantic", "disappointed", "scared",
                "frantic", "exciting", "tense", "dramatic"
        );

        for (String tagName : predefinedTags) {
            if (tagRepository.findByName(tagName).isEmpty()) {
                TagEntity tag = new TagEntity();
                tag.setName(tagName);
                tagRepository.save(tag);
            }
        }
    }
}
