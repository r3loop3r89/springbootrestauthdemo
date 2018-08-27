package com.shra1.javamster.controllers;

import com.shra1.javamster.models.UserMaster;
import com.shra1.javamster.models.UserMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class Rest {

   @Autowired
   UserMasterRepository userMasterRepository;

   @Autowired
   BCryptPasswordEncoder bCryptPasswordEncoder;

   @RequestMapping("/api/add")
   public void addUser(@RequestBody UserMaster userMaster) {
      userMaster.setCreated_on_epoch(System.currentTimeMillis());
      userMasterRepository.save(userMaster);
   }

   @RequestMapping("/add")
   public void addUserX(@RequestBody UserMaster userMaster) {
      userMaster.setCreated_on_epoch(System.currentTimeMillis());
      String pass = userMaster.getPassword();
      userMaster.setPassword(bCryptPasswordEncoder.encode(pass));
      userMasterRepository.save(userMaster);
   }

   @GetMapping("/api/greet/{name}")
   public String greetAuth(@PathVariable("name") String name) {
      return "Hello " + name +" sir!!";
   }


   @GetMapping("/greet/{name}")
   public String greetNoAuth(@PathVariable("name") String name) {
      return "Hi " + name;
   }


}
