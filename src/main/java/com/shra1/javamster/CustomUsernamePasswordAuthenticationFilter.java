package com.shra1.javamster;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shra1.javamster.dto.LoginRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
   @Override
   public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
      UsernamePasswordAuthenticationToken authRequest = null;

      LoginRequest lr = getLoginRequest(request);

      authRequest = new UsernamePasswordAuthenticationToken(lr.getUsername(), lr.getPassword());

      setDetails(request, authRequest);

      return this.getAuthenticationManager().authenticate(authRequest);
   }

   private LoginRequest getLoginRequest(HttpServletRequest request) {
      BufferedReader reader = null;
      LoginRequest loginRequest = null;

      ObjectMapper jsonMapper = new ObjectMapper();
      try {
         reader = request.getReader();
         loginRequest = jsonMapper.readValue(reader, LoginRequest.class);
      } catch (RuntimeException e) {
         throw new RuntimeException("JsonParserException " + e.getMessage());
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } finally {
         try {
            reader.close();
         } catch (IOException ex) {
            logger.error("Exception Ocuured while closing the reader", ex);
         }
      }

      if (loginRequest == null) {
         loginRequest = new LoginRequest();
      }
      return loginRequest;
   }
}
