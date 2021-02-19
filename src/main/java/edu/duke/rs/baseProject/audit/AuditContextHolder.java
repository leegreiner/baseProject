package edu.duke.rs.baseProject.audit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class AuditContextHolder {
  private static final ThreadLocal<AuditContext> contextHolder = new ThreadLocal<>();
  
  public void clearContext() {
    contextHolder.remove();
  }
  
  public AuditContext getContext() {
    AuditContext ctx = contextHolder.get();
    
    if (ctx == null) {
      ctx = new AuditContext();
      contextHolder.set(ctx);
    }
    
    return ctx;
  }
  
  @Getter
  @Setter
  @NoArgsConstructor
  public class AuditContext {
    private String reasonForChange;
  }
}
