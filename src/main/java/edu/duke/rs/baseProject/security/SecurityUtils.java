package edu.duke.rs.baseProject.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
	public static boolean userIsAuthenticated() {
		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		
		return auth != null && auth.isAuthenticated() &&
				!(SecurityContextHolder.getContext().getAuthentication() 
				          instanceof AnonymousAuthenticationToken);
	}
	
	public static AppPrincipal getPrincipal() {
    AppPrincipal appPrincipal = null;
    Authentication auth = SecurityContextHolder.getContext()
        .getAuthentication();

    if (auth != null
        && auth.getPrincipal() != null
        && AppPrincipal.class.isAssignableFrom(auth.getPrincipal()
            .getClass())) {
      appPrincipal = (AppPrincipal) (auth.getPrincipal());
    }

    return appPrincipal;
  }
}
