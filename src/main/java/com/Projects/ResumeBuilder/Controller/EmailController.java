package com.Projects.ResumeBuilder.Controller;

import com.Projects.ResumeBuilder.Service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.Projects.ResumeBuilder.Utilities.AppConstants.EMAIL;
import static com.Projects.ResumeBuilder.Utilities.AppConstants.SEND_RESUME;

@RestController
@RequestMapping(EMAIL)
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = SEND_RESUME,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> sendResumeByEmail(
            @RequestPart("recipientEmail") String recipientEmail,
            @RequestPart("subject") String subject,
            @RequestPart("message") String message,
            @RequestPart("pdfFile")MultipartFile pdfFile,
            Authentication authentication
            ) throws IOException, MessagingException {
        Map<String,Object> response = new HashMap<>();
        if(Objects.isNull(recipientEmail) || Objects.isNull(pdfFile)){
            response.put("success",false);
            response.put("message","Missing required fields");
            return ResponseEntity.badRequest().body(response);
        }

        byte[] pdfBytes = pdfFile.getBytes();
        String originalFileName = pdfFile.getOriginalFilename();
        String fileName = Objects.nonNull(originalFileName)?originalFileName:"resume.pdf";

        String emailSubject = Objects.nonNull(subject)?subject:"Resume Application";
        String emailBody = Objects.nonNull(message)?subject:"Please find my resume attached. \n\n Best Regards";

        emailService.sendEmailWithAttachment(recipientEmail,emailSubject,emailBody,pdfBytes,fileName);

        response.put("success",true);
        response.put("message","Resume sent successfully to "+recipientEmail);
        return ResponseEntity.ok(response);
    }

}
