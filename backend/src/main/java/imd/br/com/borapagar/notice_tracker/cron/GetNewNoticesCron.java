package imd.br.com.borapagar.notice_tracker.cron;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import imd.br.com.borapagar.notice_tracker.entities.Notice;
import imd.br.com.borapagar.notice_tracker.entities.Subscription;
import imd.br.com.borapagar.notice_tracker.helpers.IEmailHelper;
import imd.br.com.borapagar.notice_tracker.helpers.MailRequest;
import imd.br.com.borapagar.notice_tracker.repositories.NoticeRepository;
import imd.br.com.borapagar.notice_tracker.repositories.SubscriptionRepository;
import imd.br.com.borapagar.notice_tracker.trackers.INoticeTracker;
import imd.br.com.borapagar.notice_tracker.trackers.impl.IMDNoticeTrackerImpl;
import jakarta.mail.MessagingException;

@Component
public class GetNewNoticesCron {
    private ArrayList<INoticeTracker> noticeTrackers;

    private NoticeRepository noticeRepository;
    private SubscriptionRepository subscriptionRepository;
    private TemplateEngine templateEngine;
    private IEmailHelper emailHelper;

    @Value("${app.activate-cron}")
    private Boolean isCronActive;

    @Value("${app.unsubscribe-url}")
    private String unsubscribeUrl;

    public GetNewNoticesCron(
        NoticeRepository noticeRepository,
        SubscriptionRepository subscriptionRepository,
        TemplateEngine templateEngine,
        IEmailHelper emailHelper,
        IMDNoticeTrackerImpl imdNoticeTracker
    ) {
        this.templateEngine = templateEngine;
        this.noticeRepository = noticeRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.emailHelper = emailHelper;

        this.noticeTrackers = new ArrayList<>();
        noticeTrackers.add(imdNoticeTracker);
    }

    /**
     * Ação agendada que realiza a procura por novos editais e notifica os usuários
     * caso tenha algum edital novo.
     */
    @Scheduled(fixedDelay = 5000)
    public void getNewNoticesCron() {
        if(isCronActive) {
            ArrayList<Notice> newNotices = new ArrayList<>();
            for(INoticeTracker tracker : noticeTrackers) {
                newNotices.addAll(tracker.getNewNotices());
            }
            if(!newNotices.isEmpty()) {
                noticeRepository.saveAll(newNotices);
                notifyAllUsers(newNotices); 
            }
        } else {
            System.out.println("Cron is not active. If you dont want that, change application.properties");
        }
    }

    /**
     * Notifica todos os emails cadastrados no sistema sobre a existência
     * de novos editais.
     * @param newNotices - Lista de editais novos.
     */
    private void notifyAllUsers(List<Notice> newNotices) {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        final String EMAIL_SUBJECT = String.format("%d novos editais abertos", newNotices.size());
        for(Subscription subscription : subscriptions) {
            String to = subscription.getEmail();
            String content = generateEmailContent(newNotices, subscription.getRemoveToken());
            MailRequest noticesEmail = MailRequest.builder().to(to).subject(EMAIL_SUBJECT).content(content).build();
            try {
                emailHelper.sendEmail(noticesEmail);

            } catch (MessagingException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Gera o conteúdo do email personalizado contendo todos os editais novos e o token de 
     * remoção daquele email.
     * @param newNotices - Lista de editais novos.
     * @param removeToken - Token de remoção.
     * @return
     */
    private String generateEmailContent(List<Notice> newNotices, String removeToken) {
        Context context = new Context();
        context.setVariable("notices", newNotices);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);
        String processedText =  templateEngine.process("email-template", context);
        return processedText;
    }
}
