package com.shra1.javamster.dto;

import java.io.Serializable;

public class LoginFailureResponse implements Serializable {
   private String message;

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
