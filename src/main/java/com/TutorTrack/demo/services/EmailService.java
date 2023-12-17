package com.TutorTrack.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendOtpEmail(String to, int otpCode, String name) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Your OTP Code");

        // Load the HTML template
        Context context = new Context();
        context.setVariable("otpCode", otpCode);
        context.setVariable("name", name);
        String htmlContent = templateEngine.process("otp_email_template", context);

        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

}
