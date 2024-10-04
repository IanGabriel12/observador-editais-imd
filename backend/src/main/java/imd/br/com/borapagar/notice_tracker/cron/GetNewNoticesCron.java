package imd.br.com.borapagar.notice_tracker.cron;

import java.util.ArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import imd.br.com.borapagar.notice_tracker.entities.Notice;
import imd.br.com.borapagar.notice_tracker.entities.NoticeRepository;
import imd.br.com.borapagar.notice_tracker.trackers.INoticeTracker;
import imd.br.com.borapagar.notice_tracker.trackers.impl.IMDNoticeTrackerImpl;

@Component
public class GetNewNoticesCron {
    private ArrayList<INoticeTracker> noticeTrackers;
    private NoticeRepository noticeRepository;

    public GetNewNoticesCron(
        NoticeRepository noticeRepository,
        IMDNoticeTrackerImpl imdNoticeTracker
    ) {
        this.noticeRepository = noticeRepository;
        this.noticeTrackers = new ArrayList<>();
        noticeTrackers.add(imdNoticeTracker);
    }

    @Scheduled(fixedRate = 5000)
    public void getNewNoticesCron() {
        ArrayList<Notice> newNotices = new ArrayList<>();
        for(INoticeTracker tracker : noticeTrackers) {
            newNotices.addAll(tracker.getNewNotices());
        }
        System.out.println(newNotices.size());
        noticeRepository.saveAll(newNotices);
    }
}
