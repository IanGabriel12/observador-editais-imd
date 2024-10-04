package imd.br.com.borapagar.notice_tracker.repositories;

import org.springframework.stereotype.Repository;

import imd.br.com.borapagar.notice_tracker.entities.Subscription;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{
    Optional<Subscription> findByEmail(String email);
    Optional<Subscription> findByRemoveToken(String token);
}
