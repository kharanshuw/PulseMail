package com.example.demo.controller;

import com.example.demo.dto.CustomResponseBuilder;
import com.example.demo.model.EmailRequest;
import com.example.demo.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ApiController {

    Logger logger = LoggerFactory.getLogger(ApiController.class);

    private EmailService emailService;

    @Autowired
    public ApiController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Sends an email with the specified request details.
     *
     * @param emailRequest The email request object containing recipient, subject,
     *                     and message.
     * @return {@link ResponseEntity} containing the response status and message.
     * @throws IllegalArgumentException If required fields in {@code emailRequest}
     *                                  are missing.
     * @throws RuntimeException         If an error occurs while sending the email.
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {

        // ✅ Validate Request Inputs to ensure required fields are not empty

        logger.info("Received request to send email: {}", emailRequest);

        if (emailRequest.getToString() == null || emailRequest.getToString().isBlank()) {
            logger.warn("Validation failed: Recipient email is empty.");

            return ResponseEntity.badRequest().body("Recipient email is required.");
        }

        if (emailRequest.getSubjectString() == null || emailRequest.getSubjectString().isBlank()) {

            logger.warn("Validation failed: Email subject is empty.");

            return ResponseEntity.badRequest().body("Email subject is required.");
        }

        if (emailRequest.getMessageString() == null || emailRequest.getMessageString().isBlank()) {

            logger.warn("Validation failed: Email message is empty.");

            return ResponseEntity.badRequest().body("Email message is required.");
        }

        // ✅ Call the email service to send an email with the provided details

        logger.info("Sending email to: {}, Subject: {}", emailRequest.getToString(), emailRequest.getSubjectString());

        emailService.sendEmail(emailRequest.getToString(), emailRequest.getSubjectString(),
                emailRequest.getMessageString(), "kharanshuw@gmail.com");

        logger.info("Email sent successfully to {}", emailRequest.getToString());

        // ✅ Prepare a custom response object to indicate success

        CustomResponseBuilder customResponseBuilder = new CustomResponseBuilder();

        customResponseBuilder.setStatus(HttpStatus.OK);

        customResponseBuilder.setMessage("email send succssfully");

        customResponseBuilder.setSuccess(true);

        return ResponseEntity.ok(customResponseBuilder);
    }


    /**
     * Sends an email with a file attachment.
     * <p>
     * This method validates the incoming email request and file before processing.
     * It then invokes the {@link EmailService} to send the email.
     * </p>
     *
     * @param emailRequest The email request containing recipient, subject, and message.
     * @param file         The attached file to be sent along with the email.
     * @return {@link ResponseEntity} containing the response status and message.
     * @throws IllegalArgumentException If required fields in {@code emailRequest} are missing.
     * @throws IOException              If there is an issue processing the attached file.
     */
    @PostMapping("/send_email_with_file")
    public ResponseEntity<CustomResponseBuilder> sendEmailWithFile(@RequestPart("emailrequest") EmailRequest emailRequest,
                                                                   @RequestPart("file") MultipartFile file) throws IOException {


        logger.info("Received request to send email with file: {}", file.getOriginalFilename());


        try {

            // ✅ Validate the email request to ensure required fields are provided

            if (emailRequest.getToString() == null || emailRequest.getToString().isBlank()) {

                logger.warn("Validation failed: Recipient email is empty.");

                CustomResponseBuilder customResponseBuilder = new CustomResponseBuilder();

                customResponseBuilder.setMessage("Recipient email is required!");

                customResponseBuilder.setStatus(HttpStatus.BAD_REQUEST);

                customResponseBuilder.setSuccess(false);

                return ResponseEntity.badRequest().body(customResponseBuilder);
            }
            if (emailRequest.getSubjectString() == null || emailRequest.getSubjectString().isBlank()) {
                logger.warn("Validation failed: Email subject is empty.");

                CustomResponseBuilder customResponseBuilder = new CustomResponseBuilder();

                customResponseBuilder.setMessage("Email subject is required!");

                customResponseBuilder.setStatus(HttpStatus.BAD_REQUEST);

                customResponseBuilder.setSuccess(false);

                return ResponseEntity.badRequest().body(customResponseBuilder);
            }
            if (emailRequest.getMessageString() == null || emailRequest.getMessageString().isBlank()) {
                logger.warn("Validation failed: Email message is empty.");

                CustomResponseBuilder customResponseBuilder = new CustomResponseBuilder();

                customResponseBuilder.setMessage("Email message is required!");

                customResponseBuilder.setStatus(HttpStatus.BAD_REQUEST);

                customResponseBuilder.setSuccess(false);


                return ResponseEntity.badRequest().body(customResponseBuilder);
            }
            if (file == null || file.isEmpty()) {
                logger.warn("Validation failed: File attachment is missing.");

                CustomResponseBuilder customResponseBuilder = new CustomResponseBuilder();

                customResponseBuilder.setMessage("File attachment is required!");

                customResponseBuilder.setStatus(HttpStatus.BAD_REQUEST);

                customResponseBuilder.setSuccess(false);

                return ResponseEntity.badRequest().body(customResponseBuilder);

            }

            logger.info("Attempting to send email to: {}, Subject: {}", emailRequest.getToString(), emailRequest.getSubjectString());

            // ✅ Extract Content Type (MIME Type)

            String contentType = file.getContentType();

            logger.info("file extention type is " + contentType);

            // ✅ Remove unwanted characters & get the actual extension

            String extention = contentType.substring(contentType.lastIndexOf("/") + 1);

            logger.info("actual extention is " + extention);

            // ✅ Construct full filename with extension

            String originalName = file.getOriginalFilename();

            logger.info("original filename is " + originalName);
            if (!originalName.contains(".")) {
                originalName += "." + extention;
            }

            logger.info("file name updated after adding extention " + originalName);


            // ✅ Send the email along with the file attachment

            emailService.sendEmailWithFile(emailRequest.getToString(), 
                    emailRequest.getSubjectString(), 
                    emailRequest.getMessageString(), 
                    file.getInputStream(), 
                    "kharanshuw@gmail.com", originalName);

            logger.info("Email sent successfully to {}", emailRequest.getToString());

            CustomResponseBuilder customResponseBuilder1 = new CustomResponseBuilder("email send successfully", HttpStatus.OK, true);

            return ResponseEntity.ok(customResponseBuilder1);

        } catch (IOException e) {
            // ✅ Handle file processing errors (e.g., unable to read the file input stream)

            logger.error("File processing error while sending email: {}", e.getMessage(), e);

            CustomResponseBuilder customResponseBuilder = new CustomResponseBuilder();

            customResponseBuilder.setMessage("File processing error: " + e.getMessage());

            customResponseBuilder.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            customResponseBuilder.setSuccess(false);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(customResponseBuilder);
        } catch (Exception e) {

            logger.error("Failed to send email: {}", e.getMessage(), e);

            CustomResponseBuilder customResponseBuilder = new CustomResponseBuilder();

            customResponseBuilder.setMessage("Failed to send email: " + e.getMessage());

            customResponseBuilder.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            customResponseBuilder.setSuccess(false);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(customResponseBuilder);

        }
    }
}
