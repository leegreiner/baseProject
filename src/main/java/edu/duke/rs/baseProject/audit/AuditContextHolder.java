package edu.duke.rs.baseProject.audit;

import java.util.Optional;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuditContextHolder {
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
  
  @NoArgsConstructor
  public static class AuditContext {
    private final SecurityUtils securityUtils = new SecurityUtils();
    @Getter
    @Setter
    private String reasonForChange;
    
    public Optional<AppPrincipal> getCurrentUser() {
      return securityUtils.getPrincipal();
    }
  }
}
