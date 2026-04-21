package com.example.feedbackapp.dto;

public class TopicInsightResponse {
    private String topic;
    private Long feedbackCount;
    private Double averageRating;

    public TopicInsightResponse(String topic, Long feedbackCount, Double averageRating) {
        this.topic = topic;
        this.feedbackCount = feedbackCount;
        this.averageRating = averageRating;
    }

    public String getTopic() { return topic; }
    public Long getFeedbackCount() { return feedbackCount; }
    public Double getAverageRating() { return averageRating; }
}
