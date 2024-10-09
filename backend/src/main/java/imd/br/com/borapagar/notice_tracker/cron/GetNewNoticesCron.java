package imd.br.com.borapagar.notice_tracker.cron;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(GetNewNoticesCron.class);

    @Value("${app.activate-cron}")
    private Boolean isCronActive;

    @Value("${app.unsubscribe-uri}")
    private String UNSUBSCRIBE_URI;

    @Value("${app.frontend-url}")
    private String FRONTEND_URL;

    private final static long TEN_MINUTES_IN_MILLISECONDS = 10 * 60 * 1000;
    private final static long FIVE_SECONDS_IN_MILLISECONDS = 5*1000;

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
    @Scheduled(fixedDelay = TEN_MINUTES_IN_MILLISECONDS, initialDelay = FIVE_SECONDS_IN_MILLISECONDS)
    public void getNewNoticesCron() {
        if(isCronActive) {
            ArrayList<Notice> newNotices = new ArrayList<>();
            for(INoticeTracker tracker : noticeTrackers) {
                newNotices.addAll(tracker.getNewNotices());
            }
            if(!newNotices.isEmpty()) {
                logger.atInfo().log(String.format("Cron job found %d new notices. Notifying users...", newNotices.size()));
                noticeRepository.saveAll(newNotices);
                notifyAllUsers(newNotices); 
            } else {
                logger.atInfo().log("Cron did not find any new notices");
            }
        } else {
            logger.atWarn().log("Cron is not active. If this is not intended, change application.properties file");
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
                logger.atError().log(e.getMessage());
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
        String unsubscribeUrl = String.format("%s/%s?token=%s", FRONTEND_URL, UNSUBSCRIBE_URI, removeToken);
        context.setVariable("notices", newNotices);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);
        String processedText =  templateEngine.process("email-template", context);
        return processedText;
    }
}
