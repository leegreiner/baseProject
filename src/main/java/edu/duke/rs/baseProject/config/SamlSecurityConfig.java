package edu.duke.rs.baseProject.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileECPImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.ResourceUtils;

import edu.duke.rs.baseProject.error.ApplicationErrorController;
import edu.duke.rs.baseProject.security.AjaxAwareExceptionMappingAuthenticationHandler;
import groovy.util.ResourceException;

@Profile("samlSecurity")
@Configuration
public class SamlSecurityConfig {
  @Value("${saml.keystore.location}")
  private String samlKeystoreLocation;
  @Value("${saml.keystore.password}")
  private String samlKeystorePassword;
  @Value("${saml.keystore.alias}")
  private String samlKeystoreAlias;
  @Value("${saml.idp}")
  private String defaultIdp;
  @Value("${saml.logoutRedirectUrl}")
  private String logoutRedirectUrl;
  @Autowired
  private SAMLUserDetailsService samlUserDetailsService;

  @Bean(initMethod = "initialize")
  public StaticBasicParserPool parserPool() {
    return new StaticBasicParserPool();
  }

  @Bean
  public SAMLAuthenticationProvider samlAuthenticationProvider() {
    final SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
    samlAuthenticationProvider.setUserDetails(samlUserDetailsService);
    samlAuthenticationProvider.setForcePrincipalAsString(false);
    return samlAuthenticationProvider;
  }

  @Bean
  public SAMLContextProviderImpl contextProvider() {
    return new SAMLContextProviderImpl();
  }

  @Bean
  public static SAMLBootstrap samlBootstrap() {
    return new SAMLBootstrap();
  }

  @Bean
  public SAMLDefaultLogger samlLogger() {
    return new SAMLDefaultLogger();
  }

  @Bean
  public WebSSOProfileConsumer webSSOprofileConsumer() {
    return new WebSSOProfileConsumerImpl();
  }

  @Bean
  @Qualifier("hokWebSSOprofileConsumer")
  public WebSSOProfileConsumerHoKImpl hokWebSSOProfileConsumer() {
    return new WebSSOProfileConsumerHoKImpl();
  }

  @Bean
  public WebSSOProfile webSSOprofile() {
    return new WebSSOProfileImpl();
  }

  @Bean
  public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
    return new WebSSOProfileConsumerHoKImpl();
  }

  @Bean
  public WebSSOProfileECPImpl ecpProfile() {
    return new WebSSOProfileECPImpl();
  }

  @Bean
  public SingleLogoutProfile logoutProfile() {
    return new SingleLogoutProfileImpl();
  }

  @Bean
  public KeyManager keyManager() {
    final DefaultResourceLoader loader = new DefaultResourceLoader();
    final Resource storeFile = loader.getResource(samlKeystoreLocation);
    final Map<String, String> passwords = new HashMap<>();
    passwords.put(samlKeystoreAlias, samlKeystorePassword);
    return new JKSKeyManager(storeFile, samlKeystorePassword, passwords, samlKeystoreAlias);
  }

  @Bean
  public WebSSOProfileOptions defaultWebSSOProfileOptions() {
    final WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
    webSSOProfileOptions.setIncludeScoping(false);
    return webSSOProfileOptions;
  }
  
  @Bean
  public SAMLDiscovery samlDiscovery() {
    final SAMLDiscovery idpDiscovery = new SAMLDiscovery();
    return idpDiscovery;
  }

  @Bean
  public SAMLEntryPoint samlEntryPoint() {
    final SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
    samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
    return samlEntryPoint;
  }

  @Bean
  public ExtendedMetadata extendedMetadata() {
    final ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    extendedMetadata.setIdpDiscoveryEnabled(false);
    extendedMetadata.setSignMetadata(false);
    return extendedMetadata;
  }

  @Bean
  @Qualifier("duke")
  public ExtendedMetadataDelegate dukeExtendedMetadataProvider() throws MetadataProviderException {
    File metadata = null;
    try {
      metadata = ResourceUtils.getFile("classpath:saml/metadata/duke-metadata-2-signed.xml");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    final FilesystemMetadataProvider provider = new FilesystemMetadataProvider(metadata);
    provider.setParserPool(parserPool());
    
    final ExtendedMetadataDelegate delegate = new ExtendedMetadataDelegate(provider, extendedMetadata());
    delegate.setMetadataTrustCheck(false);
    
    return delegate;
  }

  @Bean
  @Qualifier("metadata")
  public CachingMetadataManager metadata() throws MetadataProviderException, ResourceException {
    final List<MetadataProvider> providers = new ArrayList<>();
    providers.add(dukeExtendedMetadataProvider());
    final CachingMetadataManager metadataManager = new CachingMetadataManager(providers);
    metadataManager.setDefaultIDP(defaultIdp);
    return metadataManager;
  }

  @Bean
  @Qualifier("saml")
  public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
    final SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    successRedirectHandler.setDefaultTargetUrl("/home");
    return successRedirectHandler;
  }
  
  @Bean
  @Qualifier("saml")
  public AuthenticationFailureHandler samlAuthFailureHandler() {
    final AjaxAwareExceptionMappingAuthenticationHandler handler = new AjaxAwareExceptionMappingAuthenticationHandler();
    final Map<String, String> failureMap = new HashMap<>();
    
    failureMap.put(AccountExpiredException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=accountExpired");
    failureMap.put(CredentialsExpiredException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=credentialsExpired");
    failureMap.put(DisabledException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=accountDisabled");
    failureMap.put(LockedException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=accountLocked");
    failureMap.put(UsernameNotFoundException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=accountNotFound");
    
    handler.setExceptionMappings(failureMap);
    handler.setDefaultFailureUrl(ApplicationErrorController.ERROR_MAPPING);
    
    return handler;
  }

  @Bean
  public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
    final SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
    successLogoutHandler.setDefaultTargetUrl(logoutRedirectUrl);
    return successLogoutHandler;
  }

  @Bean
  public SecurityContextLogoutHandler logoutHandler() {
    final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    logoutHandler.setInvalidateHttpSession(true);
    logoutHandler.setClearAuthentication(true);
    return logoutHandler;
  }

  @Bean
  public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
    return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
  }

  @Bean
  public SAMLLogoutFilter samlLogoutFilter() {
    return new SAMLLogoutFilter(successLogoutHandler(), new LogoutHandler[] { logoutHandler() },
        new LogoutHandler[] { logoutHandler() });
  }

  @Bean
  public HTTPPostBinding httpPostBinding() {
    return new HTTPPostBinding(parserPool(), VelocityFactory.getEngine());
  }

  @Bean
  public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
    return new HTTPRedirectDeflateBinding(parserPool());
  }

  @Bean
  public SAMLProcessorImpl processor() {
    final ArrayList<SAMLBinding> bindings = new ArrayList<>();
    bindings.add(httpRedirectDeflateBinding());
    bindings.add(httpPostBinding());
    return new SAMLProcessorImpl(bindings);
  }
  
  //Needed to get Spring Session and SAML to work without error.
  @Bean
  public CookieSerializer cookieSerializer() {
    final DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setSameSite(null);
    return serializer;
  }
 
}
