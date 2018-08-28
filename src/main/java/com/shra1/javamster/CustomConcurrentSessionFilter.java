package com.shra1.javamster;

import com.shra1.javamster.dto.GenericJsonResponse;
import com.shra1.javamster.dto.JsonAuthenticationResponseWriterUtil;
import com.shra1.javamster.dto.LoginDomainSpecificStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomConcurrentSessionFilter extends ConcurrentSessionFilter {

   SessionRegistry sessionRegistry;
   String expiredUrl;
   LogoutHandler[] handlers = new LogoutHandler[]{new SecurityContextLogoutHandler()};
   RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
   public static final int HTTP_STATUS_AUTHENTICATION_TIMEOUT = 419;

   public CustomConcurrentSessionFilter(SessionRegistry sessionRegistry) {
      super(sessionRegistry);
      Assert.notNull(sessionRegistry, "SessionRegistry required");
      this.sessionRegistry = sessionRegistry;
   }

   public CustomConcurrentSessionFilter(SessionRegistry sessionRegistry, String expiredUrl) {
      super(sessionRegistry, expiredUrl);
      Assert.notNull(sessionRegistry, "SessionRegistry required");
      Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
        expiredUrl + " isn't a valid redirect URL");
      this.sessionRegistry = sessionRegistry;
      this.expiredUrl = expiredUrl;
   }

   @Override
   public void afterPropertiesSet() {
      Assert.notNull(sessionRegistry, "SessionRegistry required");
      Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
        expiredUrl + " isn't a valid redirect URL");
   }

   @Override
   public void
   doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
     throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      HttpSession session = request.getSession(false);

      if (session!=null){
         SessionInformation info = sessionRegistry.getSessionInformation(session.getId());
         if (info != null) {
            if (info.isExpired()){
               doLogout(request, response);

               String targetUrl = determineExpiredUrl(request, info);

               if (targetUrl!=null){
                  redirectStrategy.sendRedirect(request, response, targetUrl);
                  return;
               }else{
                  response.setStatus(HTTP_STATUS_AUTHENTICATION_TIMEOUT);
                  JsonAuthenticationResponseWriterUtil.writeJsonModelToHttpServletResponse(response,
                    new GenericJsonResponse(LoginDomainSpecificStatus.SESSION_EXPIRED,
                      "This session has been expired (possibly due to multiple concurrent "
                        + "logins being attempted as the same user)."));
               }
               return;
            }else{
               // Non-expired - update last request date/time
               sessionRegistry.refreshLastRequest(info.getSessionId());
            }
         }
      }
      chain.doFilter(request, response);
   }
   protected String determineExpiredUrl(HttpServletRequest request, SessionInformation info) {
      return expiredUrl;
   }
   private void doLogout(HttpServletRequest request, HttpServletResponse response) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      for (LogoutHandler handler : handlers) {
         handler.logout(request, response, auth);
      }
   }

   public void setLogoutHandlers(LogoutHandler[] handlers) {
      Assert.notNull(handlers);
      this.handlers = handlers;
   }

   public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
      this.redirectStrategy = redirectStrategy;
   }
}
