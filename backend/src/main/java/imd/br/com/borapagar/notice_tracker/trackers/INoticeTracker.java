package imd.br.com.borapagar.notice_tracker.trackers;

import java.util.List;

import imd.br.com.borapagar.notice_tracker.entities.Notice;


public interface INoticeTracker {

    /**
     * Retorna a lista de editais novos presentes na fonte.
     * Este método deve garantir que os editais retornados não estão presentes no banco de dados.
     * @return Lista de novos editais
     */
    public List<Notice> getNewNotices();
}
