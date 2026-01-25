package com.fullstackquiz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fullstackquiz.model.Question;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for QuizService.
 */
@SpringBootTest
class QuizServiceTest {

  @Autowired
  private QuizService quizService;

  @BeforeEach
  void setUp() {
    quizService.init();
  }

  @Test
  void testGetRandomQuestionsSpring() {
    List<Question> questions = quizService.getRandomQuestions("Spring", 10);

    assertNotNull(questions);
    assertEquals(10, questions.size());

    for (Question question : questions) {
      assertNotNull(question.getQuestion());
      assertNotNull(question.getOptions());
      assertEquals(4, question.getOptions().length);
      assertTrue(question.getCorrectIndex() >= 0);
      assertTrue(question.getCorrectIndex() <= 3);
    }
  }

  @Test
  void testGetRandomQuestionsAngular() {
    List<Question> questions = quizService.getRandomQuestions("Angular", 20);

    assertNotNull(questions);
    assertEquals(20, questions.size());

    for (Question question : questions) {
      assertNotNull(question.getQuestion());
      assertNotNull(question.getOptions());
      assertEquals(4, question.getOptions().length);
    }
  }

  @Test
  void testGetRandomQuestionsMaxCount() {
    List<Question> questions = quizService.getRandomQuestions("Spring", 30);

    assertNotNull(questions);
    assertEquals(30, questions.size());
  }

  @Test
  void testGetRandomQuestionsExceedsAvailable() {
    List<Question> questions = quizService.getRandomQuestions("Spring", 1000);

    assertNotNull(questions);
    assertTrue(questions.size() > 0);
    assertTrue(questions.size() <= 1000);
  }

  @Test
  void testGetRandomQuestionsInvalidTechnology() {
    List<Question> questions = quizService.getRandomQuestions("InvalidTech", 10);

    assertNotNull(questions);
    assertTrue(questions.isEmpty());
  }

  @Test
  void testGetRandomQuestionsRandomization() {
    List<Question> firstSet = quizService.getRandomQuestions("Spring", 10);
    List<Question> secondSet = quizService.getRandomQuestions("Spring", 10);

    assertNotNull(firstSet);
    assertNotNull(secondSet);
    assertEquals(10, firstSet.size());
    assertEquals(10, secondSet.size());

    // Check that at least some questions are different (randomization)
    boolean hasDifference = false;
    for (int i = 0; i < firstSet.size(); i++) {
      if (!firstSet.get(i).getQuestion().equals(secondSet.get(i).getQuestion())) {
        hasDifference = true;
        break;
      }
    }
    assertTrue(hasDifference, "Questions should be randomized");
  }

  @Test
  void testGetAllQuestionsSpring() {
    List<Question> questions = quizService.getAllQuestions("Spring");

    assertNotNull(questions);
    assertFalse(questions.isEmpty());
    assertTrue(questions.size() >= 30);
  }

  @Test
  void testGetAllQuestionsAngular() {
    List<Question> questions = quizService.getAllQuestions("Angular");

    assertNotNull(questions);
    assertFalse(questions.isEmpty());
    assertTrue(questions.size() >= 30);
  }

  @Test
  void testCaseInsensitiveTechnology() {
    List<Question> lowerCase = quizService.getRandomQuestions("spring", 10);
    List<Question> upperCase = quizService.getRandomQuestions("SPRING", 10);
    List<Question> mixedCase = quizService.getRandomQuestions("SpRiNg", 10);

    assertEquals(10, lowerCase.size());
    assertEquals(10, upperCase.size());
    assertEquals(10, mixedCase.size());
  }
}
