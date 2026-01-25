package com.fullstackquiz.controller;

import com.fullstackquiz.model.Question;
import com.fullstackquiz.service.QuizService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for quiz operations.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QuizController {
  private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

  private final QuizService quizService;

  public QuizController(QuizService quizService) {
    this.quizService = quizService;
  }

  /**
   * Get random questions for a given technology and count.
   *
   * @param technology the technology name (Spring or Angular)
   * @param count the number of questions to return (10, 20, or 30)
   * @return list of random questions
   */
  @GetMapping("/questions/{technology}/{count}")
  public ResponseEntity<List<Question>> getQuestions(
      @PathVariable String technology,
      @PathVariable int count) {

    logger.info("Request received for {} questions on {}", count, technology);

    if (!isValidTechnology(technology)) {
      logger.warn("Invalid technology requested: {}", technology);
      return ResponseEntity.badRequest().build();
    }

    if (!isValidCount(count)) {
      logger.warn("Invalid count requested: {}", count);
      return ResponseEntity.badRequest().build();
    }

    List<Question> questions = quizService.getRandomQuestions(technology, count);

    if (questions.isEmpty()) {
      logger.warn("No questions found for {} with count {}", technology, count);
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(questions);
  }

  /**
   * Validate technology parameter.
   *
   * @param technology the technology to validate
   * @return true if valid, false otherwise
   */
  private boolean isValidTechnology(String technology) {
    return technology != null
        && (technology.equalsIgnoreCase("spring")
        || technology.equalsIgnoreCase("angular"));
  }

  /**
   * Validate count parameter.
   *
   * @param count the count to validate
   * @return true if valid, false otherwise
   */
  private boolean isValidCount(int count) {
    return count == 10 || count == 20 || count == 30;
  }
}
