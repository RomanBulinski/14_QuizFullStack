package com.fullstackquiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Application information model.
 */
@Data
@AllArgsConstructor
public class AppInfo {
  private String version;
  private String name;
}
