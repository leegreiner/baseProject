package edu.duke.rs.baseProject.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.duke.rs.baseProject.AbstractBaseTest;
import edu.duke.rs.baseProject.role.RoleName;

public class SecurityUtilsUnitTest extends AbstractBaseTest {
  @Mock
  private Authentication authentication;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private AppPrincipal appPrincipal;
  private SecurityUtils securityUtils = new SecurityUtils();
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    SecurityContextHolder.setContext(securityContext);
  }
  
  @Test
  public void whenUserIsNotAutheticated_thenUserAutheticatedIsFalse() {
    when(securityContext.getAuthentication()).thenReturn(null);
    
    assertThat(securityUtils.userIsAuthenticated(), is(false));
  }
  
  @Test
  public void whenUserIsAutheticated_thenUserAutheticatedIsTrue() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    
    assertThat(securityUtils.userIsAuthenticated(), is(true));
  }
  
  @Test
  public void whenAnonymousUserIsAutheticated_thenUserAutheticatedIsFalse() {
    when(securityContext.getAuthentication()).thenReturn(
        new AnonymousAuthenticationToken(easyRandom.nextObject(String.class),easyRandom.nextObject(String.class), AuthorityUtils
            .createAuthorityList("ROLE_" + easyRandom.nextObject(String.class), "ROLE_" + easyRandom.nextObject(String.class))));
    when(authentication.isAuthenticated()).thenReturn(true);
    
    assertThat(securityUtils.userIsAuthenticated(), is(false));
  }
  
  @Test
  public void whenUserNotAuthenticated_thenHasRoleIsFalse() {
    when(securityContext.getAuthentication()).thenReturn(null);
    
    assertThat(securityUtils.hasRole(RoleName.USER), is(false));
  }
  
  @Test
  public void whenAnonymousUserIsAuthenticated_thenHasRoleIsFalse() {
    when(securityContext.getAuthentication()).thenReturn(
        new AnonymousAuthenticationToken(easyRandom.nextObject(String.class),easyRandom.nextObject(String.class), AuthorityUtils
            .createAuthorityList("ROLE_" + easyRandom.nextObject(String.class), "ROLE_" + easyRandom.nextObject(String.class))));
    
    assertThat(securityUtils.hasRole(RoleName.USER), is(false));
  }
  
  @Test
  public void whenUserIsAuthenticatedAndHasUserRole_thenHasUserRoleIsTrue() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(appPrincipal);
    when(appPrincipal.hasRole(RoleName.USER)).thenReturn(true);
    
    assertThat(securityUtils.hasRole(RoleName.USER), is(true));
  }
  
  @Test
  public void whenAuthPrincipalIsNull_thenGetPrinciaplReturnsEmptyOptional() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(null);
    
    assertThat(securityUtils.getPrincipal(), equalTo(Optional.empty()));
  }
}
