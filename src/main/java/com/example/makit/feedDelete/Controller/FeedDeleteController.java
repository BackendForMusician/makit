package com.example.makit.feedDelete.Controller;

import com.example.makit.feedDelete.Service.FeedDeleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedDeleteController {

    private final FeedDeleteService feedDeleteService;

    public FeedDeleteController(FeedDeleteService feedDeleteService) {
        this.feedDeleteService = feedDeleteService;
    }

    @DeleteMapping("/delete/{feedId}")
    public ResponseEntity<String> deleteFeed(@PathVariable Long feedId) {
        try {
            feedDeleteService.deleteFeed(feedId);
            return ResponseEntity.ok("피드가 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
