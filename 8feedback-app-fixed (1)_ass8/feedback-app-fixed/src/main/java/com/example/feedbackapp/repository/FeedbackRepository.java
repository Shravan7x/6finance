package com.example.feedbackapp.repository;

import com.example.feedbackapp.dto.TopicInsightResponse;
import com.example.feedbackapp.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByServiceNameIgnoreCase(String serviceName);

    List<Feedback> findByTopicIgnoreCase(String topic);

    Optional<Feedback> findByCustomerEmailIgnoreCase(String customerEmail);

    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getAverageRating();

    @Query("SELECT new com.example.feedbackapp.dto.TopicInsightResponse(f.topic, COUNT(f), AVG(f.rating)) " +
            "FROM Feedback f GROUP BY f.topic ORDER BY COUNT(f) DESC")
    List<TopicInsightResponse> findTopicInsights();

    @Query("SELECT f FROM Feedback f WHERE LOWER(f.comment) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Feedback> searchByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM feedbacks f WHERE f.rating <= :rating ORDER BY f.created_at DESC", nativeQuery = true)
    List<Feedback> findLowRatedFeedback(@Param("rating") Integer rating);
}
