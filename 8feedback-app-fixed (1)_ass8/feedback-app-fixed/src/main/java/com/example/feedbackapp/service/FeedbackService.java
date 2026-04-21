package com.example.feedbackapp.service;

import com.example.feedbackapp.dto.FeedbackRequest;
import com.example.feedbackapp.dto.TopicInsightResponse;
import com.example.feedbackapp.entity.Feedback;
import com.example.feedbackapp.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Feedback saveFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setCustomerName(request.getCustomerName());
        feedback.setCustomerEmail(request.getCustomerEmail());
        feedback.setServiceName(request.getServiceName());
        feedback.setTopic(request.getTopic());
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    public List<Feedback> getByService(String serviceName) {
        return feedbackRepository.findByServiceNameIgnoreCase(serviceName);
    }

    public List<Feedback> searchByKeyword(String keyword) {
        return feedbackRepository.searchByKeyword(keyword);
    }

    public List<Feedback> getLowRatedFeedback(Integer rating) {
        return feedbackRepository.findLowRatedFeedback(rating);
    }

    public Double getAverageRating() {
        Double avg = feedbackRepository.getAverageRating();
        return avg == null ? 0.0 : avg;
    }

    public List<TopicInsightResponse> getTopicInsights() {
        return feedbackRepository.findTopicInsights();
    }
}
