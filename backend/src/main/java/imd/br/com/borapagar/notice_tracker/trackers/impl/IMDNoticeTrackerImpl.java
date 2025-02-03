package imd.br.com.borapagar.notice_tracker.trackers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import imd.br.com.borapagar.notice_tracker.entities.Notice;
import imd.br.com.borapagar.notice_tracker.exception.ApiException;
import imd.br.com.borapagar.notice_tracker.helpers.SSLHelper;
import imd.br.com.borapagar.notice_tracker.helpers.StringHelper;
import imd.br.com.borapagar.notice_tracker.repositories.NoticeRepository;
import imd.br.com.borapagar.notice_tracker.trackers.INoticeTracker;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IMDNoticeTrackerImpl implements INoticeTracker {

    @Autowired
    private NoticeRepository noticeRepository;

    private static Logger logger = LoggerFactory.getLogger(IMDNoticeTrackerImpl.class);

    private final String SOURCE_URL = "https://www.metropoledigital.ufrn.br";
    private final String NOTICES_URI = "portal/editais";
    private final String SOURCE_NAME = "IMD";

    @Override
    public List<Notice> getNewNotices() {
        List<Notice> allNotices = fetchAllNotices();
        List<Notice> newNotices = filterNewNotices(allNotices);
        return newNotices;
    }

    /**
     * Dado uma lista de editais, filtra apenas aqueles que não estão no banco de
     * dados.
     * 
     * @param notices - Lista original de editais
     * @return Lista contendo editais novos
     */
    private List<Notice> filterNewNotices(List<Notice> notices) {
        return notices.stream().filter((notice) -> !isNoticeAtDatabase(notice)).toList();
    }

    /**
     * Verifica se o edital já existe no banco de dados
     * 
     * @param notice - Edital
     * @return Um booleano
     */
    private Boolean isNoticeAtDatabase(Notice notice) {
        return noticeRepository.findBySourceNameAndIdAtSource(SOURCE_NAME, notice.getIdAtSource()).isPresent();
    }

    /**
     * Recupera todos os editais presentes na seção "Em andamento" da página de
     * editais do IMD.
     * 
     * @return Lista de editais
     */
    private List<Notice> fetchAllNotices() {
        ArrayList<Notice> notices = new ArrayList<>();
        try {
            String noticesUrl = String.format("%s/%s", SOURCE_URL, NOTICES_URI);
            Document noticeWebsite = SSLHelper.getConnection(noticesUrl).get();
            Elements noticesElements = noticeWebsite.select(".box-editais-andamentos .card .card-body a");
            for (Element noticeElement : noticesElements) {
                String noticeTitle = noticeElement.select("span").first().text();
                String noticeSubtitle = noticeElement.select("h5").first().text();
                String noticeUrl = SOURCE_URL + noticeElement.attr("href");
                String noticeDescription = this.getNoticeDescription(noticeUrl);
                String noticeDescriptionTruncatedEllipsis = StringHelper.truncateEllipsis(noticeDescription,
                        Notice.MAX_LENGTH_DESCRIPTION_IN_CHARACTERES);
                String[] noticeUrlParts = noticeUrl.split("/");

                Long idAtSource = Long.parseLong(noticeUrlParts[noticeUrlParts.length - 1]);
                Notice notice = Notice
                        .builder()
                        .url(noticeUrl)
                        .idAtSource(idAtSource)
                        .title(noticeTitle + " - " + noticeSubtitle)
                        .description(noticeDescriptionTruncatedEllipsis)
                        .sourceName(SOURCE_NAME)
                        .build();
                notices.add(notice);
            }
        } catch (IOException ex) {
            logger.atError().log("Error at fetching notices from IMD website");
            throw new ApiException(ex.getMessage());
        } catch (NumberFormatException ex) {
            throw new ApiException(ex.getMessage());
        }
        return notices;
    }

    /**
     * Recupera a descrição de um edital
     * 
     * @param noticeUrl - URL do edital
     * @return Descrição do edital
     */
    private String getNoticeDescription(String noticeUrl) {
        try {
            Document noticeDetailPage = SSLHelper.getConnection(noticeUrl).get();
            return noticeDetailPage.select("div.conteudo-noticia").first().text();
        } catch (IOException ex) {
            logger.atError().log("Error at fetching notice description from IMD website");
            throw new ApiException(ex.getMessage());
        }
    }
}
