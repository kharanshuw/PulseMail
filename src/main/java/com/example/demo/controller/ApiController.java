package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CustomResponseBuilder;
import com.example.demo.model.EmailRequest;
import com.example.demo.service.EmailService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private EmailService emailService;

    @Autowired
    public ApiController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {

        emailService.sendEmail(emailRequest.getToString(), emailRequest.getSubjectString(),
                emailRequest.getMessageString(), "kharanshuw@gmail.com");

        CustomResponseBuilder customResponseBuilder = new CustomResponseBuilder();

        customResponseBuilder.setStatus(HttpStatus.OK);

        customResponseBuilder.setMessage("email send succssfully");

        customResponseBuilder.setSuccess(true);

        return ResponseEntity.ok(customResponseBuilder);
    }
}
