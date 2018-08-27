package com.shra1.javamster.dto;

import java.io.Serializable;

public class LoginSuccessResponse implements Serializable {
   String username;

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }
}
