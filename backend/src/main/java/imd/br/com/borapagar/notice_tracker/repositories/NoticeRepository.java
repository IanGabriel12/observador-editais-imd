package imd.br.com.borapagar.notice_tracker.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import imd.br.com.borapagar.notice_tracker.entities.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>{
    public Optional<Notice> findBySourceNameAndIdAtSource(String sourceName, Long idAtSource);
}
