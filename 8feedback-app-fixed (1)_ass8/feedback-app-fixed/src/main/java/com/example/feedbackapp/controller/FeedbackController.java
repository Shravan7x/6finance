package com.example.feedbackapp.controller;

import com.example.feedbackapp.dto.FeedbackRequest;
import com.example.feedbackapp.dto.TopicInsightResponse;
import com.example.feedbackapp.entity.Feedback;
import com.example.feedbackapp.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@Valid @RequestBody FeedbackRequest request) {
        return new ResponseEntity<>(feedbackService.saveFeedback(request), HttpStatus.CREATED);
    }

    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<Feedback>> getByService(@PathVariable String serviceName) {
        return ResponseEntity.ok(feedbackService.getByService(serviceName));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Feedback>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(feedbackService.searchByKeyword(keyword));
    }

    @GetMapping("/insights/topics")
    public ResponseEntity<List<TopicInsightResponse>> getTopicInsights() {
        return ResponseEntity.ok(feedbackService.getTopicInsights());
    }

    @GetMapping("/insights/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("averageRating", feedbackService.getAverageRating());
        summary.put("lowRatedFeedback", feedbackService.getLowRatedFeedback(2));
        summary.put("topTopics", feedbackService.getTopicInsights());
        return ResponseEntity.ok(summary);
    }
}
