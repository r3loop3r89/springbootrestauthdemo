package com.shra1.javamster.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "user_master")
public class UserMaster {
   @Id()
   @GeneratedValue(strategy = GenerationType.AUTO)
   long user_id;
   long created_on_epoch;
   String email;
   String username;
   String last_name;
   String mobile;
   String password;
   String profile_picture_url;
   String role;

   public UserMaster(long created_on_epoch, String email, String username, String last_name, String mobile, String password, String profile_picture_url, String role) {
      this.created_on_epoch = created_on_epoch;
      this.email = email;
      this.username = username;
      this.last_name = last_name;
      this.mobile = mobile;
      this.password = password;
      this.profile_picture_url = profile_picture_url;
      this.role = role;
   }

   public UserMaster() {
   }

   public long getUser_id() {
      return user_id;
   }

   public void setUser_id(long user_id) {
      this.user_id = user_id;
   }

   public long getCreated_on_epoch() {
      return created_on_epoch;
   }

   public void setCreated_on_epoch(long created_on_epoch) {
      this.created_on_epoch = created_on_epoch;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getLast_name() {
      return last_name;
   }

   public void setLast_name(String last_name) {
      this.last_name = last_name;
   }

   public String getMobile() {
      return mobile;
   }

   public void setMobile(String mobile) {
      this.mobile = mobile;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getProfile_picture_url() {
      return profile_picture_url;
   }

   public void setProfile_picture_url(String profile_picture_url) {
      this.profile_picture_url = profile_picture_url;
   }

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }
}
