package edu.duke.rs.baseProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import edu.duke.rs.baseProject.config.GoogleProperties;

public abstract class BaseWebController {
  @Autowired
  private GoogleProperties googleProperties;
  
  private static final String VIEW_REDIRECT_PREFIX = "redirect:";

  protected String createRedirectViewPath(String path) {
    StringBuilder builder = new StringBuilder();
    builder.append(VIEW_REDIRECT_PREFIX);
    builder.append(path);
    return builder.toString();
  }
  
  @ModelAttribute("googleProperties")
  public GoogleProperties getGoogleProperties() {
    return this.googleProperties;
  }

}
