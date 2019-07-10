package edu.duke.rs.baseProject.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.user.User;

public class AppPrincipal implements UserDetails {
	private static final long serialVersionUID = 1L;
	private final User user;
	private final Collection<SimpleGrantedAuthority> authorities;
	
	public AppPrincipal(final User user) {
		this.user = user;
		authorities = new ArrayList<SimpleGrantedAuthority>();
		
		for (final Role role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName().name()));
		}
	}
	
	public Long getUserId() {
	  return this.user.getId();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }
	
	public String getDisplayName() {
	  return user.getDisplayName();
	}
}
