package com.shra1.javamster;

import com.shra1.javamster.dto.JsonAuthenticationResponseWriterUtil;
import com.shra1.javamster.dto.LoginFailureResponse;
import com.shra1.javamster.dto.LoginSuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   @Autowired
   SessionRegistry sessionRegistry;
   @Autowired
   CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy;
   @Autowired
   CustomConcurrentSessionFilter customConcurrentSessionFilter;


   @Autowired
   @Qualifier("customUserDetailsService")
   private UserDetailsService userDetailsService;
   @Autowired
   private BCryptPasswordEncoder bCryptPasswordEncoder;


   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/", "/login", "/logout")
        .permitAll()
        .and()
        .authorizeRequests()
        .antMatchers("/api/**")
        .authenticated()
        .and()
        .sessionManagement()
        .sessionAuthenticationStrategy(compositeSessionAuthenticationStrategy);
      http.addFilter(customConcurrentSessionFilter);
   }

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
   }

   @Override
   public void configure(WebSecurity web) throws Exception {
      web
        .ignoring()
        .antMatchers(
          "/resources/**",
          "/static/**",
          "/css/**",
          "/js/**",
          "/images/**");
   }

   @Bean
   @Override
   public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
   }

   @Bean
   public CustomUsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
      CustomUsernamePasswordAuthenticationFilter authenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
      authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
      authenticationFilter.setAuthenticationManager(authenticationManagerBean());
      authenticationFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
         @Override
         public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            if (authentication.isAuthenticated()) {
               LoginSuccessResponse loginSuccessResponse = new LoginSuccessResponse();
               Object principal = authentication.getPrincipal();

               if (principal instanceof User) {
                  User securityUserDetails = (User) principal;
                  loginSuccessResponse.setUsername(securityUserDetails.getUsername());
                  try {
                     response.setContentType("application/json");
                     response.setStatus(HttpServletResponse.SC_OK);
                     JsonAuthenticationResponseWriterUtil.writeJsonModelToHttpServletResponse(
                       response, loginSuccessResponse
                     );
                  } catch (AuthenticationServiceException e) {
                     request.getSession().invalidate();
                     response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                     e.printStackTrace();
                  }
               }
            } else {
               throw new UnsupportedOperationException("Invalid Login");
            }
         }
      });

      authenticationFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
         @Override
         public void onAuthenticationFailure(HttpServletRequest request,
                                             HttpServletResponse response,
                                             AuthenticationException exception)
           throws IOException, ServletException {

            LoginFailureResponse loginFailureResponse = new LoginFailureResponse();
            if (exception instanceof BadCredentialsException) {
               loginFailureResponse.setMessage(exception.getMessage());
            } else if (exception instanceof DisabledException) {
               loginFailureResponse.setMessage(exception.getMessage());
            } else if (exception instanceof AuthenticationServiceException) {
               loginFailureResponse.setMessage(exception.getMessage());
            }
            JsonAuthenticationResponseWriterUtil.writeJsonModelToHttpServletResponse(response,
              loginFailureResponse);
         }
      });

      authenticationFilter.setSessionAuthenticationStrategy(compositeSessionAuthenticationStrategy);

      return authenticationFilter;
   }

   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
      BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
      return bCryptPasswordEncoder;
   }

   @Bean
   SessionRegistry sessionRegistry() {
      SessionRegistry sessionRegistry = new SessionRegistryImpl();
      return sessionRegistry;
   }

   @Bean
   CustomConcurrentSessionFilter customConcurrentSessionFilter() {
      CustomConcurrentSessionFilter customConcurrentSessionFilter = new CustomConcurrentSessionFilter(sessionRegistry);
      return customConcurrentSessionFilter;
   }

   @Bean
   CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy() {
      List<SessionAuthenticationStrategy> delegateStrategies = new ArrayList<>();

      ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
      concurrentSessionControlAuthenticationStrategy.setMaximumSessions(1);
      delegateStrategies.add(concurrentSessionControlAuthenticationStrategy);
      delegateStrategies.add(new SessionFixationProtectionStrategy());
      delegateStrategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry));

      CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy
        = new CompositeSessionAuthenticationStrategy(delegateStrategies);

      return compositeSessionAuthenticationStrategy;
   }
}
