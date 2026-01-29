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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
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

    // Load from single CSV files (backward compatibility)
    loadQuestionsFromCsv("spring");
    loadQuestionsFromCsv("angular");

    // Load from subdirectories with multiple CSV files
    loadQuestionsFromDirectory("spring", DATA_PATH + "java/");
    loadQuestionsFromDirectory("angular", DATA_PATH + "angular/");

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
          if (row.length >= 6 && !isHeaderRow(row)) {
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
   * Load questions from all CSV files in a directory for a given technology.
   *
   * @param technology the technology name (e.g., "java", "angular")
   * @param directoryPath the directory path containing CSV files
   */
  private void loadQuestionsFromDirectory(String technology, String directoryPath) {
    try {
      ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource[] resources = resolver.getResources("classpath:" + directoryPath + "*.csv");

      List<Question> allQuestions = new ArrayList<>(
          questionCache.getOrDefault(technology.toLowerCase(), new ArrayList<>())
      );

      for (Resource resource : resources) {
        List<Question> questions = loadQuestionsFromResource(resource);
        allQuestions.addAll(questions);
        logger.info("Loaded {} questions from {}", questions.size(), resource.getFilename());
      }

      questionCache.put(technology.toLowerCase(), allQuestions);
      logger.info("Total loaded {} questions for {} from directory",
          allQuestions.size(), technology);
    } catch (IOException e) {
      logger.error("Error loading questions from directory {}: {}",
          directoryPath, e.getMessage());
    }
  }

  /**
   * Load questions from a single CSV resource.
   *
   * @param resource the CSV resource to load
   * @return list of questions from the resource
   */
  private List<Question> loadQuestionsFromResource(Resource resource) {
    List<Question> questions = new ArrayList<>();

    try {
      CSVParser parser = new CSVParserBuilder()
          .withSeparator(';')
          .build();

      try (CSVReader reader = new CSVReaderBuilder(
          new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
          .withCSVParser(parser)
          .build()) {

        reader.readAll().forEach(row -> {
          if (row.length >= 6 && !isHeaderRow(row)) {
            String question = row[0];
            String[] options = {row[1], row[2], row[3], row[4]};
            int correctIndex = Integer.parseInt(row[5]);
            questions.add(new Question(question, options, correctIndex));
          }
        });
      }
    } catch (IOException | CsvException | NumberFormatException e) {
      logger.error("Error loading questions from resource {}: {}",
          resource.getFilename(), e.getMessage());
    }

    return questions;
  }

  /**
   * Check if a CSV row is a header row.
   *
   * @param row the CSV row to check
   * @return true if the row is a header, false otherwise
   */
  private boolean isHeaderRow(String[] row) {
    if (row.length < 6) {
      return false;
    }

    String firstColumn = row[0].toLowerCase();
    String lastColumn = row[5].toLowerCase();

    // Check for common header patterns
    return firstColumn.contains("pytanie")
        || firstColumn.contains("question")
        || lastColumn.contains("indeks")
        || lastColumn.contains("correct")
        || lastColumn.contains("index");
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
