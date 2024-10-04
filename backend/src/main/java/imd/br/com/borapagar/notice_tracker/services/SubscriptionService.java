package imd.br.com.borapagar.notice_tracker.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import imd.br.com.borapagar.notice_tracker.controllers.dto.CreateSubscriptionBody;
import imd.br.com.borapagar.notice_tracker.controllers.dto.RemoveSubscriptionBody;
import imd.br.com.borapagar.notice_tracker.entities.Subscription;
import imd.br.com.borapagar.notice_tracker.repositories.SubscriptionRepository;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public void insertNewSubscription(CreateSubscriptionBody createSubscriptionBody) {
        String email = createSubscriptionBody.email();
        if(isEmailSubscribed(email)) {
            throw new RuntimeException("Email já cadastrado");
        }
        String removeToken = UUID.randomUUID().toString();
        Subscription subscription = Subscription
            .builder()
            .email(email)
            .removeToken(removeToken)
            .build();
        subscriptionRepository.save(subscription);
    }

    private Boolean isEmailSubscribed(String email) {
        return subscriptionRepository.findByEmail(email).isPresent();
    }

    public void removeSubscription(RemoveSubscriptionBody removeSubscriptionBody) {
        String removeToken = removeSubscriptionBody.removeToken();
        Subscription subscription = subscriptionRepository
            .findByRemoveToken(removeToken)
            .orElseThrow(() -> new RuntimeException("Token inválido"));
        subscriptionRepository.delete(subscription);
    }
}
