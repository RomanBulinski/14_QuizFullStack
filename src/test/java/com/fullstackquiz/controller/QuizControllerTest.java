package com.fullstackquiz.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fullstackquiz.model.Question;
import com.fullstackquiz.service.QuizService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for QuizController.
 */
@WebMvcTest(QuizController.class)
class QuizControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private QuizService quizService;

  @Test
  void testGetQuestionsSpring10() throws Exception {
    List<Question> mockQuestions = createMockQuestions(10);
    when(quizService.getRandomQuestions("Spring", 10)).thenReturn(mockQuestions);

    mockMvc.perform(get("/api/questions/Spring/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(10)))
        .andExpect(jsonPath("$[0].question").exists())
        .andExpect(jsonPath("$[0].options", hasSize(4)))
        .andExpect(jsonPath("$[0].correctIndex").isNumber());
  }

  @Test
  void testGetQuestionsAngular20() throws Exception {
    List<Question> mockQuestions = createMockQuestions(20);
    when(quizService.getRandomQuestions("Angular", 20)).thenReturn(mockQuestions);

    mockMvc.perform(get("/api/questions/Angular/20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(20)));
  }

  @Test
  void testGetQuestions30() throws Exception {
    List<Question> mockQuestions = createMockQuestions(30);
    when(quizService.getRandomQuestions("Spring", 30)).thenReturn(mockQuestions);

    mockMvc.perform(get("/api/questions/Spring/30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(30)));
  }

  @Test
  void testGetQuestionsInvalidTechnology() throws Exception {
    mockMvc.perform(get("/api/questions/InvalidTech/10"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetQuestionsInvalidCount() throws Exception {
    mockMvc.perform(get("/api/questions/Spring/15"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetQuestionsInvalidCountZero() throws Exception {
    mockMvc.perform(get("/api/questions/Angular/0"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetQuestionsNoQuestionsFound() throws Exception {
    when(quizService.getRandomQuestions(anyString(), anyInt())).thenReturn(new ArrayList<>());

    mockMvc.perform(get("/api/questions/Spring/10"))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetQuestionsCaseInsensitive() throws Exception {
    List<Question> mockQuestions = createMockQuestions(10);
    when(quizService.getRandomQuestions("spring", 10)).thenReturn(mockQuestions);

    mockMvc.perform(get("/api/questions/spring/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(10)));
  }

  private List<Question> createMockQuestions(int count) {
    List<Question> questions = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      String[] options = {
          "Option A " + i,
          "Option B " + i,
          "Option C " + i,
          "Option D " + i
      };
      questions.add(new Question("Question " + i, options, i % 4));
    }
    return questions;
  }
}
