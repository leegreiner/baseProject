package edu.duke.rs.baseProject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import edu.duke.rs.baseProject.security.RestAutenticationAccessDeniedHandler;
import edu.duke.rs.baseProject.security.RestBasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	private static String MGMT_USER_NAME;
	private static String MGMT_PASSWORD;
	
	@Value("${app.management.userName}")
    public void setManagementUserName(final String mgmtUserName) {
    	MGMT_USER_NAME = mgmtUserName;
    }
    
    @Value("${app.management.password}")
    public void setManagementPassword(final String mgmtPassword) {
    	MGMT_PASSWORD = mgmtPassword;
    }
	
    public static UserDetailsService appUserDetailsService() throws Exception {
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        manager.createUser(User.withUsername("user").password(passwordEncoder().encode("password")).roles("USER").build());
        
        return manager;
    }
    
    public static UserDetailsService managementUserDetailsService() throws Exception {
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        manager.createUser(User.withUsername(MGMT_USER_NAME).password(passwordEncoder().encode(MGMT_PASSWORD)).roles("MANAGEMENT").build());
        
        return manager;
    }
	
	@Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Configuration
	@Order(1)
	public static class ManagementConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Autowired
		private RestAutenticationAccessDeniedHandler accessDeniedHandler;
		@Autowired
		private RestBasicAuthenticationEntryPoint authenticationEntryPoint;
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			 auth
			 	.userDetailsService(managementUserDetailsService())
	        	.passwordEncoder(passwordEncoder());
		}
		
		@Override
        protected void configure(final HttpSecurity http) throws Exception {
			http
				.requestMatcher(EndpointRequest.toAnyEndpoint())
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
					.csrf().disable()
					.authorizeRequests().anyRequest().hasRole("MANAGEMENT")
				.and()
					.httpBasic()
						.authenticationEntryPoint(authenticationEntryPoint)
					.and()
						.exceptionHandling()
							.accessDeniedHandler(accessDeniedHandler);
		}
	}
	
	@Configuration
	@Order(2)
	public static class ApplicationConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	        auth
	        	.userDetailsService(appUserDetailsService())
	        	.passwordEncoder(passwordEncoder());
	    }
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.authorizeRequests()
					.antMatchers("/css/**", "/fonts/**", "/img/**", "/js/**").permitAll()
					.antMatchers("/").permitAll()		
					.anyRequest().hasRole("USER")
					.and()
				.formLogin()
						.loginPage("/loginPage").permitAll()
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/home", true)
						.failureUrl("/loginPage?error=loginError")
				.and()
						.logout()
							.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
							.logoutSuccessUrl("/")
							.deleteCookies("JSESSIONID")
			    			.invalidateHttpSession(true)
			    			.permitAll();
		}
	}
}
