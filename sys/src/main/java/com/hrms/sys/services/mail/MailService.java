package com.hrms.sys.services.mail;

import com.hrms.sys.responses.PayrollResponse;
import com.hrms.sys.services.payroll.PayrollService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final PayrollService payrollService;

    public void sendSimpleMessage(String[] to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendHtmlMessage(String[] to, String subject, Map<String, Object> templateModel) throws MessagingException {
        Context context = new Context();
        context.setVariables(templateModel);
        String htmlBody = templateEngine.process("newEmployee", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    public void sendMailPayroll(String[] to, String subject, Map<String, Object> templateModel) throws MessagingException {

//        LocalDate currentDate = LocalDate.now();
////
////        // Lấy tháng và năm từ currentDate
////        int currentMonth = currentDate.getMonthValue();
////        int currentYear = currentDate.getYear();
////        List<PayrollResponse> payrollResponses = new ArrayList<>();
////        payrollResponses = payrollService.getPayrollsByMonthAndYear(currentYear, currentMonth);

        // Giả sử dữ liệu lương đã được lấy từ service và trả về
        List<PayrollResponse> payrollResponses = createDummyPayrolls();

        for (PayrollResponse payroll : payrollResponses) {
            String recipientEmail = payroll.getEmail();
            String emailSubject = "Thông báo lương tháng " + payroll.getMonth() + "/" + payroll.getYear();
            String emailContent = prepareEmailContent(payroll);

            sendEmail(recipientEmail, emailSubject, emailContent);
        }
    }

    private String prepareEmailContent(PayrollResponse payroll) {
        Context context = new Context();
        context.setVariable("full_name", payroll.getFullName());
        context.setVariable("position", payroll.getPosition());
        context.setVariable("username", payroll.getUsername());
        context.setVariable("month", payroll.getMonth());
        context.setVariable("year", payroll.getYear());
        context.setVariable("basicSalary", payroll.getBasicSalary());
        context.setVariable("basicSalaryReceived", payroll.getBasicSalaryReceived());
        context.setVariable("overtimeSalary", payroll.getOvertimeSalary());
        context.setVariable("benefit", payroll.getBenefit());
        context.setVariable("totalSalary", payroll.getTotalSalary());
        context.setVariable("tax", payroll.getTax());

        return templateEngine.process("payroll", context); // Đảm bảo tồn tại email-template.html trong resources/templates
    }

    private void sendEmail(String recipientEmail, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // true để enable HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    // Hàm tạo dữ liệu cứng cho lương để test
    private List<PayrollResponse> createDummyPayrolls() {
        List<PayrollResponse> payrolls = new ArrayList<>();

        // Tạo các đối tượng PayrollResponse với dữ liệu cứng để test
        PayrollResponse payroll1 = new PayrollResponse();
        payroll1.setId(1L);
        payroll1.setUsername("emp1");
        payroll1.setFullName("Employee 1");
        payroll1.setEmail("duc.leanh0312@gmail.com");
        payroll1.setPosition("Developer");
        payroll1.setMonth(6);
        payroll1.setYear(2024);
        payroll1.setBasicSalary(5000F);
        payroll1.setBasicSalaryReceived(4500F);
        payroll1.setOvertimeSalary(1000F);
        payroll1.setBenefit(500F);
        payroll1.setTotalSalary(6000F);
        payroll1.setTax(1000F);
        payrolls.add(payroll1);

        PayrollResponse payroll2 = new PayrollResponse();
        payroll2.setId(2L);
        payroll2.setUsername("emp2");
        payroll2.setFullName("Employee 2");
        payroll2.setEmail("duc.la0312@gmail.com");
        payroll2.setPosition("Tester");
        payroll2.setMonth(6);
        payroll2.setYear(2024);
        payroll2.setBasicSalary(4500F);
        payroll2.setBasicSalaryReceived(4000F);
        payroll2.setOvertimeSalary(800F);
        payroll2.setBenefit(400F);
        payroll2.setTotalSalary(5200F);
        payroll2.setTax(800F);
        payrolls.add(payroll2);

        return payrolls;
    }

}
