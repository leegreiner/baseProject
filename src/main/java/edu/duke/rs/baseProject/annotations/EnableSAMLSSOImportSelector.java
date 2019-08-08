package edu.duke.rs.baseProject.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import com.github.ulisesbocchio.spring.boot.security.saml.configuration.SAMLServiceProviderSecurityConfiguration;

public class EnableSAMLSSOImportSelector implements ImportSelector, EnvironmentAware {
  private Environment environment;
  
  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    final AnnotationAttributes attributes = AnnotationAttributes.fromMap(
        importingClassMetadata.getAnnotationAttributes(
            EnableSAMLSSOWhenProfileActive.class.getName(), false));
    final String profile = attributes.getString("value");
    final List<String> activeProfiles = new ArrayList<String>();
    Collections.addAll(activeProfiles, environment.getActiveProfiles());
    String[] result = new String[0];
    
    if (activeProfiles.contains(profile)) {
      result = new String[] {SAMLServiceProviderSecurityConfiguration.class.getName()};
    }
    
    return result;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
}
