package edu.duke.rs.baseProject.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EnableSAMLSSOImportSelector.class)
public @interface EnableSAMLSSOWhenProfileActive {
    String value () default "";
}
