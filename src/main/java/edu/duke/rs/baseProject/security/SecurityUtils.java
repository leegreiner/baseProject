package edu.duke.rs.baseProject.security;

import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
	public boolean userIsAuthenticated() {
		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		
		return auth != null && auth.isAuthenticated() &&
				!(SecurityContextHolder.getContext().getAuthentication() 
				          instanceof AnonymousAuthenticationToken);
	}
	
	public Optional<AppPrincipal> getPrincipal() {
    AppPrincipal appPrincipal = null;
    final Authentication auth = SecurityContextHolder.getContext()
        .getAuthentication();

    if (auth != null
        && auth.getPrincipal() != null
        && AppPrincipal.class.isAssignableFrom(auth.getPrincipal()
            .getClass())) {
      appPrincipal = (AppPrincipal) (auth.getPrincipal());
    }

    return appPrincipal == null ? Optional.empty() : Optional.of(appPrincipal);
  }
}
