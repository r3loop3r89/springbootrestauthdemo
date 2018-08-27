package com.shra1.javamster;

import com.shra1.javamster.models.UserMaster;
import com.shra1.javamster.models.UserMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

   @Autowired
   UserMasterRepository userMasterRepository;

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      UserMaster user = userMasterRepository.findByUsername(username);

      if (user == null) {
         throw new UsernameNotFoundException("No user found with username: " + username);
      }

      return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
        .password(user.getPassword()).roles(user.getRole()).build();
   }
}
