package edu.duke.rs.baseProject.email;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class MessageContentBuilder {
  private static final String TEMPLATE_BASE = "email/";
  private transient final TemplateEngine templateEngine;
  
  public MessageContentBuilder(final TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }
  
  public String build(final MessageType messageType, Map<String, Object> content) {
    final Context context = new Context();
    
    content.forEach((name, value) -> context.setVariable(name, value.toString()));
    
    return templateEngine.process(TEMPLATE_BASE + messageType.getName(), context);
  }
}
