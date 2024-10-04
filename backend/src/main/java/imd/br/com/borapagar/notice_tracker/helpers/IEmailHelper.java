package imd.br.com.borapagar.notice_tracker.helpers;

import org.springframework.scheduling.annotation.Async;

import jakarta.mail.MessagingException;

public interface IEmailHelper {

    /**
     * Envia um email para o usuário
     * @param mailRequest - Dados do email, incluindo destinatário, assunto e conteúdo
     * @throws MessagingException
     */
    @Async
    public void sendEmail(MailRequest mailRequest) throws MessagingException;
}
