package com.fullstackquiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a quiz question.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
  private String question;
  private String[] options;
  private int correctIndex;
}
