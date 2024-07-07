package com.hrms.sys.controllers;


import com.hrms.sys.responses.PayrollResponse;
import com.hrms.sys.services.mail.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/mails")
@RequiredArgsConstructor
public class EmailController {
    private final MailService mailService;

    @GetMapping("/sendEmail")
    public String sendEmail(@RequestParam String[] to, @RequestParam String subject, @RequestParam String text) {
        mailService.sendSimpleMessage(to, subject, text);
        return "Email sent successfully";
    }

    @GetMapping("/sendHtmlEmail")
    public String sendHtmlEmail() {
        try {
            // Cấu hình thông tin email mẫu để test
            String[] to = {"duc.la0312@gmail.com"};
            String subject = "Welcome to Staff Zen";
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("full_name", "Nguyen Van A");
            templateModel.put("start_date", "2024-06-13");
            templateModel.put("end_date", "2025-06-13");
            templateModel.put("position", "Software Engineer");
            templateModel.put("department", "IT");
            templateModel.put("username", "nguyenvana");
            templateModel.put("password", "12345678");

            // Gửi email sử dụng dịch vụ email
            mailService.sendHtmlMessage(to, subject, templateModel);
            return "Email sent successfully";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send email";
        }
    }

    @GetMapping("/send-email")
    public ResponseEntity<String> sendEmailPayroll() {
        try {
            // Dữ liệu cứng để test
            String[] recipients = {"employee1@example.com", "employee2@example.com"};
            String subject = "Thông báo lương tháng";
            Map<String, Object> templateModel = new HashMap<>();

            mailService.sendMailPayroll(recipients, subject, templateModel);

            return ResponseEntity.ok("Emails sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send emails: " + e.getMessage());
        }
    }


}
