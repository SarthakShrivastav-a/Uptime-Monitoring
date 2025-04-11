package com.product.uptime.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendEmailWithAttachment(String recipient, String body, String subject, byte[] pdfAttachment, String fileName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(from);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);

            String safeFileName = StringUtils.hasText(fileName) ? fileName : "attachment.pdf";

            if (pdfAttachment != null && pdfAttachment.length > 0) {
                helper.addAttachment(safeFileName, new ByteArrayDataSource(pdfAttachment, "application/pdf"));
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", recipient, e);
        }
    }
    @Async
    public void sendEmail(String recipient, String body, String subject){
    System.out.println("Reached Here");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(recipient);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }
}

//        package com.basic.bank.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//
//    @Autowired
//    private JavaMailSender javaMailSender;
//
//    @Value("${spring.mail.username}")
//    private String from;
//
//    @Async
//    public void sendEmail(String recipient, String body, String subject){
//
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom(from);
//        simpleMailMessage.setTo(recipient);
//        simpleMailMessage.setSubject(subject);
//        simpleMailMessage.setText(body);
//        javaMailSender.send(simpleMailMessage);
//    }
//}