package edu.duke.rs.baseProject.security.saml;

import java.time.LocalDateTime;
import java.util.Optional;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("samlSecurity")
@Service
public class SamlUserDetailsServiceImpl implements SAMLUserDetailsService {
  public static final String UID_ATTRIBUTE = "urn:oid:0.9.2342.19200300.100.1.1";
  /*
  public static final String EMAIL_ATTRIBUTE = "urn:oid:0.9.2342.19200300.100.1.3";
  public static final String GIVEN_NAME_ATTRIBUTE = "urn:oid:2.5.4.42";
  public static final String MIDDLE_NAME_ATTRIBUTE = "urn:mace:duke.edu:idms:middle-name1";
  public static final String SUR_NAME_ATTRIBUTE = "urn:oid:2.5.4.4";
  public static final String DUKE_UNIQUE_ID_ATTRIBUTE = "urn:mace:duke.edu:idms:unique-id";
  */
  private transient final UserRepository userRepository;
  
  public SamlUserDetailsServiceImpl(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
    final String userName = getSamlAttribute(credential, UID_ATTRIBUTE)
        .orElseThrow(() -> new UsernameNotFoundException("User name attribute note found"));
    
    log.debug("Received credentials for " + userName);
    
    final User user = this.userRepository.findByUserNameIgnoreCase(userName)
        .orElseThrow(() -> new UsernameNotFoundException("User " + userName + " not found"));
    
    if (! user.isAccountEnabled()) {
      throw new LockedException("User " + userName + "'s account is disabled");
    }

    user.setLastLoggedIn(LocalDateTime.now());
    
    return new AppPrincipal(user, false);
  }
  
  private Optional<String> getSamlAttribute(final SAMLCredential credential, final String attrName) {
    final Attribute attr = credential.getAttribute(attrName);
    
    if (attr == null) {
      return Optional.empty();
    }
    
    String attributeValue = null;

    final XMLObject object = attr.getAttributeValues().get(0);
    if (object instanceof XSString) {
      attributeValue = ((XSString) object).getValue();
    } else if (object instanceof XSAny) {
      attributeValue = ((XSAny) object).getTextContent();
    }

    return attributeValue == null ? Optional.empty() : Optional.of(attributeValue);
  }
}
