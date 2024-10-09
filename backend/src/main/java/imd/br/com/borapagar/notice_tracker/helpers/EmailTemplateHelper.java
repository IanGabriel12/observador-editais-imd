package imd.br.com.borapagar.notice_tracker.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import imd.br.com.borapagar.notice_tracker.entities.Notice;
import java.util.List;
/**
 * Classe responsável por gerar os emails personalizados para cada usuário
 */
@Component
public class EmailTemplateHelper {
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.unsubscribe-uri}")
    private String UNSUBSCRIBE_URI;

    @Value("${app.frontend-url}")
    private String FRONTEND_URL;

    /**
     * Gera o conteúdo do email personalizado contendo todos os editais novos e o token de 
     * remoção daquele email.
     * @param newNotices - Lista de editais novos.
     * @param removeToken - Token de remoção.
     * @return Conteúdo do email gerado (em HTML)
     */
    public String generateNoticesEmailContent(List<Notice> newNotices, String removeToken) {
        Context context = new Context();
        String unsubscribeUrl = generateUnsubscribeUrlFromToken(removeToken);
        context.setVariable("notices", newNotices);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);
        String processedText =  templateEngine.process("email-template", context);
        return processedText;
    }

    /**
     * Gera o conteúdo do email de boas vindas do usuário.
     * @param removeToken - Token de remoção para geração da URL de desinscrição
     * @return Conteúdo do email
     */
    public String generateWelcomeEmailContent(String removeToken) {
        Context context = new Context();
        String unsubscribeUrl = generateUnsubscribeUrlFromToken(removeToken);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);
        String processedText =  templateEngine.process("welcome-template", context);
        return processedText;
    }

    /**
     * Gera o link para desinscrever o usuário do sistema dado o seu token de remoção.
     * @param removeToken - Token de remoção
     * @return Link para desiscrever o usuário gerado a partir do token
     */
    private String generateUnsubscribeUrlFromToken(String removeToken) {
        return String.format("%s/%s?token=%s", FRONTEND_URL, UNSUBSCRIBE_URI, removeToken);
    }
}
