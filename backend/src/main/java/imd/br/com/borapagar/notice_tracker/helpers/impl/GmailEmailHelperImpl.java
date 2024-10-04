package imd.br.com.borapagar.notice_tracker.helpers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import imd.br.com.borapagar.notice_tracker.helpers.IEmailHelper;
import imd.br.com.borapagar.notice_tracker.helpers.MailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class GmailEmailHelperImpl implements IEmailHelper {
    @Autowired JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    /**
     * Envia um email através do serviço de SMTP do Gmail
     */
    @Override
    public void sendEmail(MailRequest mailRequest) throws MessagingException {
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(fromMail);
        mimeMessageHelper.setTo(mailRequest.to());
        mimeMessageHelper.setSubject(mailRequest.subject());
        mimeMessageHelper.setText(mailRequest.content(), true);
        mailSender.send(mimeMessage);
    }
    
}
