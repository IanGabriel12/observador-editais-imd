package imd.br.com.borapagar.notice_tracker.trackers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import imd.br.com.borapagar.notice_tracker.entities.Notice;
import imd.br.com.borapagar.notice_tracker.exception.ApiException;
import imd.br.com.borapagar.notice_tracker.helpers.SSLHelper;
import imd.br.com.borapagar.notice_tracker.repositories.NoticeRepository;
import imd.br.com.borapagar.notice_tracker.trackers.INoticeTracker;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Service
public class IMDNoticeTrackerImpl implements INoticeTracker {

    @Autowired
    private NoticeRepository noticeRepository;

    private final String SOURCE_URL = "https://www.metropoledigital.ufrn.br/portal/editais";
    private final String SOURCE_NAME = "IMD";

    @Override
    public List<Notice> getNewNotices() {
        List<Notice> allNotices = fetchAllNotices();
        List<Notice> newNotices = filterNewNotices(allNotices);
        return newNotices;
    }

    private List<Notice> filterNewNotices(List<Notice> notices) {
        return notices.stream().filter((notice) -> !isNoticeAtDatabase(notice)).toList();
    }

    private Boolean isNoticeAtDatabase(Notice notice) {
        return noticeRepository.findBySourceNameAndIdAtSource(SOURCE_NAME, notice.getIdAtSource()).isPresent();
    } 
    

    private List<Notice> fetchAllNotices() {
        ArrayList<Notice> notices = new ArrayList<>();
        try {
            Document noticeWebsite = SSLHelper.getConnection(SOURCE_URL).get();
            Elements noticesElements = noticeWebsite.select(".box-editais-andamentos .card .card-body a");
            for(Element noticeElement : noticesElements) {
                String noticeTitle = noticeElement.select("span").first().text();
                String noticeSubtitle = noticeElement.select("h5").first().text();
                String noticeUrl = SOURCE_URL + noticeElement.attr("href");
                String[] noticeUrlParts = noticeUrl.split("/");

                Long idAtSource = Long.parseLong(noticeUrlParts[noticeUrlParts.length - 1]);
                Notice notice = Notice
                    .builder()
                    .url(noticeUrl)
                    .idAtSource(idAtSource)
                    .title(noticeTitle + " - " + noticeSubtitle)
                    .sourceName(SOURCE_NAME)
                    .build();
                notices.add(notice);
            }
        } catch(IOException ex) {
            throw new ApiException(ex.getMessage());
        } catch (NumberFormatException ex) {
            throw new ApiException(ex.getMessage());
        }
        return notices;
    }
}
