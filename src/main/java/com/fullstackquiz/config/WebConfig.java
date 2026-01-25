package com.fullstackquiz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Web configuration for serving static resources and SPA routing.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * Configure resource handlers for static content and SPA fallback.
   *
   * @param registry the resource handler registry
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/static/")
        .resourceChain(true)
        .addResolver(new PathResourceResolver() {
          @Override
          protected Resource getResource(String resourcePath, Resource location) throws IOException {
            Resource requestedResource = location.createRelative(resourcePath);

            if (requestedResource.exists() && requestedResource.isReadable()) {
              return requestedResource;
            }

            // Fallback to index.html for SPA routing
            return new ClassPathResource("/static/index.html");
          }
        });
  }
}
