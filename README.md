# Base Project
A Spring Boot base application with SmartAdmin. Supports database and SAML authentication.

# Authentication
To switch from database to SAML authentication add @EnableSAMLSSO to the BaseProjectApplication class and run with the samlSecurity spring boot profile.
SAML configuration properties are found in application.yml.
