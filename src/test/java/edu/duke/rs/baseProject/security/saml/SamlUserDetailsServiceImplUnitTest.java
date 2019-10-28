package edu.duke.rs.baseProject.security.saml;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSBoolean;
import org.opensaml.xml.schema.XSString;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

public class SamlUserDetailsServiceImplUnitTest {
  private static final String USER_NAME = "abc";
  @Mock
  private UserRepository userRepository;
  @Mock
  private User user;
  @Mock
  private SAMLCredential credential;
  @Mock
  private Attribute uidAttribute;
  @Mock
  private XSString xmlObject;
  @InjectMocks
  private SamlUserDetailsServiceImpl samlUserDetailsService;
  private static XMLObjectBuilderFactory builderFactory;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test(expected = UsernameNotFoundException.class)
  public void whenUserAttributeNotFound_thenUsernameNotFoundExceptionThrown() throws Exception {
    when(credential.getAttribute(SamlUserDetailsServiceImpl.UID_ATTRIBUTE)).thenReturn(null);
    when(uidAttribute.getAttributeValues()).thenReturn(Collections.singletonList(createXSStringAttributeValue()));
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.empty());
    
    samlUserDetailsService.loadUserBySAML(credential);
  }
  
  @Test(expected = UsernameNotFoundException.class)
  public void whenUserIdValueNotFound_thenUsernameNotFoundExceptionThrown() throws Exception {
    when(credential.getAttribute(SamlUserDetailsServiceImpl.UID_ATTRIBUTE)).thenReturn(uidAttribute);
    when(uidAttribute.getAttributeValues()).thenReturn(Collections.singletonList(createXSBooleanAttributeValue()));
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.empty());
    
    samlUserDetailsService.loadUserBySAML(credential);
  }
  
  @Test(expected = UsernameNotFoundException.class)
  public void whenUserNotFound_thenUsernameNotFoundExceptionThrown() throws Exception {
    when(credential.getAttribute(SamlUserDetailsServiceImpl.UID_ATTRIBUTE)).thenReturn(uidAttribute);
    when(uidAttribute.getAttributeValues()).thenReturn(Collections.singletonList(createXSStringAttributeValue()));
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.empty());
    
    samlUserDetailsService.loadUserBySAML(credential);
  }
  
  @Test(expected = LockedException.class)
  public void whenUserFoundButInactive_thenLockedExceptionThrown() throws Exception {
    when(credential.getAttribute(SamlUserDetailsServiceImpl.UID_ATTRIBUTE)).thenReturn(uidAttribute);
    when(uidAttribute.getAttributeValues()).thenReturn(Collections.singletonList(createXSStringAttributeValue()));
    when(user.isAccountEnabled()).thenReturn(false);
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
    
    samlUserDetailsService.loadUserBySAML(credential);
  }
  
  @Test
  public void whenUserFoundAndUidAttributeXSString_appPrincipalReturned() throws Exception {
    when(credential.getAttribute(SamlUserDetailsServiceImpl.UID_ATTRIBUTE)).thenReturn(uidAttribute);
    when(uidAttribute.getAttributeValues()).thenReturn(Collections.singletonList(createXSStringAttributeValue()));
    when(user.isAccountEnabled()).thenReturn(true);
    when(user.getUsername()).thenReturn(USER_NAME);
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
    
    final Object principal = samlUserDetailsService.loadUserBySAML(credential);
    
    assertThat(principal,instanceOf(AppPrincipal.class));
    final AppPrincipal appPrincipal = (AppPrincipal) principal;
    assertThat(appPrincipal.getUsername(), equalTo(USER_NAME));
    verify(userRepository, times(1)).findByUsernameIgnoreCase(USER_NAME);
    verify(user, times(1)).setLastLoggedIn(any());
  }
  
  @Test
  public void whenUserFoundAndUidAttributeXSAny_appPrincipalReturned() throws Exception {
    when(credential.getAttribute(SamlUserDetailsServiceImpl.UID_ATTRIBUTE)).thenReturn(uidAttribute);
    when(uidAttribute.getAttributeValues()).thenReturn(Collections.singletonList(createXSAnyAttributeValue()));
    when(user.isAccountEnabled()).thenReturn(true);
    when(user.getUsername()).thenReturn(USER_NAME);
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
    
    final Object principal = samlUserDetailsService.loadUserBySAML(credential);
    
    assertThat(principal,instanceOf(AppPrincipal.class));
    final AppPrincipal appPrincipal = (AppPrincipal) principal;
    assertThat(appPrincipal.getUsername(), equalTo(USER_NAME));
    verify(userRepository, times(1)).findByUsernameIgnoreCase(USER_NAME);
    verify(user, times(1)).setLastLoggedIn(any());
  }
  
  private static XSBoolean createXSBooleanAttributeValue() throws Exception {
    @SuppressWarnings("unchecked")
    final XMLObjectBuilder<XSBoolean> builder = getSAMLBuilder().getBuilder(XSBoolean.TYPE_NAME);
    final XSBoolean result = builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSBoolean.TYPE_NAME);
    return result;
  }
  
  private static XSString createXSStringAttributeValue() throws Exception {
    @SuppressWarnings("unchecked")
    final XMLObjectBuilder<XSString> builder = getSAMLBuilder().getBuilder(XSString.TYPE_NAME);
    final XSString result = builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
    result.setValue(USER_NAME);
    return result;
  }
  
  private static XSAny createXSAnyAttributeValue() throws Exception {
    @SuppressWarnings("unchecked")
    final XMLObjectBuilder<XSAny> builder = getSAMLBuilder().getBuilder(XSAny.TYPE_NAME);
    final XSAny result = builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSAny.TYPE_NAME);
    result.setTextContent(USER_NAME);
    return result;
  }
  
  private static XMLObjectBuilderFactory getSAMLBuilder() throws ConfigurationException {
    if(builderFactory == null){
      DefaultBootstrap.bootstrap();
      builderFactory = Configuration.getBuilderFactory();
    }

    return builderFactory;
  }
}
