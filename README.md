# Base Project
A Spring Boot base application with SmartAdmin. Supports database and SAML authentication.

# Authentication
To switch from database to SAML authentication add @EnableSAMLSSO to the BaseProjectApplication class and run with the samlSecurity spring boot profile.
SAML configuration properties are found in application.yml.

# Running
Add the ENCRYPT_KEY value as an environment variable and run with the the following VM arguments:

--add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED -Dhazelcast.ignoreXxeProtectionFailures=true

The hazelcast.ignoreXxeProtectionFailures property is needed to prevent Javamelody errors when inspecting caches. Spring SAML pulls in xerces and xalan which causes the issue.
