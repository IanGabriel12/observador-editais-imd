package imd.br.com.borapagar.notice_tracker.services;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import imd.br.com.borapagar.notice_tracker.controllers.dto.CreateSubscriptionBody;
import imd.br.com.borapagar.notice_tracker.controllers.dto.RemoveSubscriptionBody;
import imd.br.com.borapagar.notice_tracker.entities.Subscription;
import imd.br.com.borapagar.notice_tracker.exception.ApiException;
import imd.br.com.borapagar.notice_tracker.helpers.EmailTemplateHelper;
import imd.br.com.borapagar.notice_tracker.helpers.IEmailHelper;
import imd.br.com.borapagar.notice_tracker.helpers.MailRequest;
import imd.br.com.borapagar.notice_tracker.repositories.SubscriptionRepository;
import jakarta.mail.MessagingException;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EmailTemplateHelper emailTemplateHelper;

    @Autowired
    private IEmailHelper emailHelper;

    private static Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    /**
     * Inscreve o usuário no sistema. Adicionando-o no banco de dados e enviando-lhe
     * um email de boas vindas.
     * @param createSubscriptionBody - Corpo da requisição contendo o email do usuário
     */
    public void subscribeEmail(CreateSubscriptionBody createSubscriptionBody) {
        Subscription subscription = insertNewSubscription(createSubscriptionBody.email());
        sendWelcomeEmail(subscription.getEmail(), subscription.getRemoveToken());
    }

    /**
     * Insere uma nova entidade representando uma inscrição no banco de dados.
     * Este método valida se o email é único.
     * @param email - Email a ser cadastrado
     */
    private Subscription insertNewSubscription(String email) {
        if(isEmailSubscribed(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email aready subscribed");
        }
        String removeToken = UUID.randomUUID().toString();
        Subscription subscription = Subscription
            .builder()
            .email(email)
            .removeToken(removeToken)
            .build();
        return subscriptionRepository.save(subscription);
    }

    /**
     * Envia email de boas vindas para o usuário
     * @param to - Email de destino
     * @param removeToken - Token de remoção do email. Utilizado para a geração do link de desinscrição
     */
    private void sendWelcomeEmail(String to, String removeToken) {
        final String EMAIL_SUBJECT = "Boas vindas";
        String content = emailTemplateHelper.generateWelcomeEmailContent(removeToken);
        MailRequest welcomeEmail = MailRequest.builder().to(to).subject(EMAIL_SUBJECT).content(content).build();
        try {
            emailHelper.sendEmail(welcomeEmail);
        } catch (MessagingException e) {
            logger.atError().log(e.getMessage());
        }
    }

    /**
     * Checa se o email está presente no banco de dados
     * @param email - Email a ser verificado
     * @return booleano indicando se o email existe no banco ou não
     */
    private Boolean isEmailSubscribed(String email) {
        return subscriptionRepository.findByEmail(email).isPresent();
    }

    /**
     * Dado um token, remove a inscrição relacionada a este token.
     * @param removeSubscriptionBody - Dados do corpo da requisição. Contém o token
     */
    public void removeSubscription(RemoveSubscriptionBody removeSubscriptionBody) {
        String removeToken = removeSubscriptionBody.removeToken();
        Subscription subscription = subscriptionRepository
            .findByRemoveToken(removeToken)
            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid token"));
        subscriptionRepository.delete(subscription);
    }
}
