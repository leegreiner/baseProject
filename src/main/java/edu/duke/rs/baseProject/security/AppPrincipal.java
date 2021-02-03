package edu.duke.rs.baseProject.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.user.User;

public class AppPrincipal implements UserDetails {
	private static final long serialVersionUID = 1L;
	static final String ROLE_PREFIX = "ROLE_";
	private final User user;
	private final boolean passwordExpired;
	private final boolean accountLocked;
	private final Collection<SimpleGrantedAuthority> authorities;
	
	public AppPrincipal(final User user, final boolean passwordExpired, final boolean accountLocked) {
		this.user = user;
		this.passwordExpired = passwordExpired;
		this.accountLocked = accountLocked;
		authorities = new HashSet<SimpleGrantedAuthority>();
		
		for (final Role role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName().name()));
			if (role.getPrivileges() != null) {
  			role.getPrivileges().stream()
  			  .forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege.getName().name())));
			}
		}
	}
	
	public boolean hasRole(final RoleName roleName) {
    boolean result = false;
    
    for (final GrantedAuthority authority : this.getAuthorities()) {
      if ((ROLE_PREFIX + roleName.toString()).equals(authority.getAuthority())) {
        result = true;
        break;
      }
    }
    
    return result;
  }
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return ! accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !passwordExpired;
	}

	@Override
	public boolean isEnabled() {
		return user.isAccountEnabled();
	}
	
	@Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }
	
	public TimeZone getTimeZone() {
	  return this.user.getTimeZone();
	}
	
	public void setTimeZone(TimeZone timeZone) {
	  this.user.setTimeZone(timeZone);
	}
	
	public Long getUserId() {
    return this.user.getId();
  }

	public UUID getAlternateUserId() {
    return this.user.getAlternateId();
  }
	
	public String getDisplayName() {
	  return this.user.getDisplayName();
	}
	
	public String getEmail() {
	  return this.user.getEmail();
	}
}
