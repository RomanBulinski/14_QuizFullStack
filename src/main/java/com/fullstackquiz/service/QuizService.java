package com.fullstackquiz.service;

import com.fullstackquiz.model.Question;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * Service class for managing quiz questions.
 */
@Service
public class QuizService {
  private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
  private static final String DATA_PATH = "data/";
  private static final String CSV_EXTENSION = "-questions.csv";

  private final Map<String, List<Question>> questionCache = new HashMap<>();

  /**
   * Initialize the question cache on application startup.
   */
  @PostConstruct
  public void init() {
    logger.info("Initializing question cache...");
    loadQuestionsFromCsv("spring");
    loadQuestionsFromCsv("angular");
    logger.info("Question cache initialized successfully");
  }

  /**
   * Load questions from CSV file for a given technology.
   *
   * @param technology the technology name (e.g., "spring", "angular")
   */
  private void loadQuestionsFromCsv(String technology) {
    String fileName = DATA_PATH + technology.toLowerCase() + CSV_EXTENSION;
    List<Question> questions = new ArrayList<>();

    try {
      ClassPathResource resource = new ClassPathResource(fileName);
      CSVParser parser = new CSVParserBuilder()
          .withSeparator(';')
          .build();

      try (CSVReader reader = new CSVReaderBuilder(
          new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
          .withCSVParser(parser)
          .build()) {

        reader.readAll().forEach(row -> {
          if (row.length >= 6) {
            String question = row[0];
            String[] options = {row[1], row[2], row[3], row[4]};
            int correctIndex = Integer.parseInt(row[5]);
            questions.add(new Question(question, options, correctIndex));
          }
        });

        questionCache.put(technology.toLowerCase(), questions);
        logger.info("Loaded {} questions for {}", questions.size(), technology);
      }
    } catch (IOException | CsvException e) {
      logger.error("Error loading questions for {}: {}", technology, e.getMessage());
      questionCache.put(technology.toLowerCase(), new ArrayList<>());
    }
  }

  /**
   * Get random questions for a given technology and count.
   *
   * @param technology the technology name (e.g., "Spring", "Angular")
   * @param count the number of questions to return
   * @return list of random questions
   */
  public List<Question> getRandomQuestions(String technology, int count) {
    String techKey = technology.toLowerCase();
    List<Question> allQuestions = questionCache.get(techKey);

    if (allQuestions == null || allQuestions.isEmpty()) {
      logger.warn("No questions found for technology: {}", technology);
      return new ArrayList<>();
    }

    if (count > allQuestions.size()) {
      logger.warn("Requested count {} exceeds available questions {}. Returning all.",
          count, allQuestions.size());
      count = allQuestions.size();
    }

    List<Question> shuffled = new ArrayList<>(allQuestions);
    Collections.shuffle(shuffled);
    return shuffled.subList(0, count);
  }

  /**
   * Get all available questions for a technology.
   *
   * @param technology the technology name
   * @return list of all questions
   */
  public List<Question> getAllQuestions(String technology) {
    String techKey = technology.toLowerCase();
    return new ArrayList<>(questionCache.getOrDefault(techKey, new ArrayList<>()));
  }
}
