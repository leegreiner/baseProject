package edu.duke.rs.baseProject.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.junit.Test;

import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.user.User;

public class AppPrinicipalUnitTest {
  @Test
  public void whenPasswordExpired_thenIsCredentialsNonExpiredIsFalse() {
    final User user = new User();
    user.setRoles(new HashSet<Role>());
    final AppPrincipal appPrincipal = new AppPrincipal(user, true, true);
    
    assertThat(appPrincipal.isCredentialsNonExpired(), equalTo(false));
    assertThat(appPrincipal.isAccountNonLocked(), equalTo(false));
  }
  
  @Test
  public void whenPasswordNotExpired_thenIsCredentialsNonExpiredIsTrue() {
    final User user = new User();
    user.setRoles(new HashSet<Role>());
    final AppPrincipal appPrincipal = new AppPrincipal(user, false, false);
    
    assertThat(appPrincipal.isCredentialsNonExpired(), equalTo(true));
    assertThat(appPrincipal.isAccountNonLocked(), equalTo(true));
  }
  
  @Test
  public void whenUsersIdProvided_thenGetUserIdReturnsId() {
    final User user = new User();
    user.setRoles(new HashSet<Role>());
    user.setId(Long.valueOf(1));
    final AppPrincipal appPrincipal = new AppPrincipal(user, false, false);
    
    assertThat(appPrincipal.getUserId(), equalTo(user.getId()));
  }
  
  @Test
  public void verifyAllOtherFields() {
    final User user = new User();
    user.setRoles(new HashSet<Role>());
    user.getRoles().add(new Role(RoleName.USER));
    user.setAccountEnabled(true);
    
    AppPrincipal appPrincipal = new AppPrincipal(user, false, false);
    
    assertThat(appPrincipal.isEnabled(), equalTo(true));
    assertThat(appPrincipal.isAccountNonLocked(), equalTo(true));
    assertThat(appPrincipal.isAccountNonExpired(), equalTo(true));
    
    user.setAccountEnabled(false);
    
    appPrincipal = new AppPrincipal(user, true, true);
    assertThat(appPrincipal.isEnabled(), equalTo(false));
    assertThat(appPrincipal.isAccountNonLocked(), equalTo(false));
    assertThat(appPrincipal.isAccountNonExpired(), equalTo(true));
  }
}
