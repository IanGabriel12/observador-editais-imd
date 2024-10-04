package imd.br.com.borapagar.notice_tracker.cron;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import imd.br.com.borapagar.notice_tracker.entities.Notice;
import imd.br.com.borapagar.notice_tracker.repositories.NoticeRepository;
import imd.br.com.borapagar.notice_tracker.trackers.INoticeTracker;
import imd.br.com.borapagar.notice_tracker.trackers.impl.IMDNoticeTrackerImpl;

@Component
public class GetNewNoticesCron {
    private ArrayList<INoticeTracker> noticeTrackers;
    private NoticeRepository noticeRepository;

    @Value("${app.activate-cron}")
    private Boolean isCronActive;

    public GetNewNoticesCron(
        NoticeRepository noticeRepository,
        IMDNoticeTrackerImpl imdNoticeTracker
    ) {
        this.noticeRepository = noticeRepository;
        this.noticeTrackers = new ArrayList<>();
        noticeTrackers.add(imdNoticeTracker);
    }

    @Scheduled(fixedDelay = 5000)
    public void getNewNoticesCron() {
        if(isCronActive) {
            ArrayList<Notice> newNotices = new ArrayList<>();
            for(INoticeTracker tracker : noticeTrackers) {
                newNotices.addAll(tracker.getNewNotices());
            }
            System.out.println(newNotices.size());
            noticeRepository.saveAll(newNotices);
        } else {
            System.out.println("Cron está desativado. Se você não quer isso, mude o application.properties");
        }
    }
}
