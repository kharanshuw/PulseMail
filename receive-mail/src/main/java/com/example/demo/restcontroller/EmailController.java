package com.example.demo.restcontroller;


import com.example.demo.dto.responsedto.EmailDto;
import com.example.demo.service.Emailservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/emails")
public class EmailController {
    
    private Emailservice emailservice;

    @Autowired
    public EmailController(Emailservice emailservice) {
        this.emailservice = emailservice;
    }


    @GetMapping
    public ResponseEntity<List<EmailDto>> getEmails(@RequestParam(required = false) String sender,
                                                    @RequestParam(required = false) String subject,
                                                    @RequestParam(required = false) Boolean unread,
                                                    @RequestParam(required = false, defaultValue = "10") int limit,
                                                    @RequestParam(required = false) String sinceDate)

    {
       List<EmailDto> emailDtos = emailservice.fetchEmails(sender,subject,unread,limit,sinceDate);
       
       ResponseEntity<List<EmailDto>> responseEntity = new ResponseEntity<>(emailDtos,HttpStatus.OK);
       
       return responseEntity;
    }



    @PatchMapping("/{uid}/read")
    public ResponseEntity<String> markAsRead(@PathVariable long uid) {
        emailservice.markEmailReadStatus(uid,true);
        return ResponseEntity.ok("Email marked as read.");
    }

    @PatchMapping("/{uid}/unread")
    public ResponseEntity<String> markAsUnread(@PathVariable long uid) {
       emailservice.markEmailReadStatus(uid,false);
        return ResponseEntity.ok("Email marked as unread.");
    }

}
