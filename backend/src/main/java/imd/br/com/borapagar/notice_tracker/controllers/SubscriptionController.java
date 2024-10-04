package imd.br.com.borapagar.notice_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imd.br.com.borapagar.notice_tracker.controllers.dto.CreateSubscriptionBody;
import imd.br.com.borapagar.notice_tracker.controllers.dto.RemoveSubscriptionBody;
import imd.br.com.borapagar.notice_tracker.services.SubscriptionService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
    
    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<String> subscribeEmail(@RequestBody @Valid CreateSubscriptionBody subscriptionDTO) {
        subscriptionService.insertNewSubscription(subscriptionDTO);
        return ResponseEntity.ok().body("Email registrado com sucesso");
    }

    @DeleteMapping
    public ResponseEntity<String> removeSubscription(@RequestBody @Valid RemoveSubscriptionBody removeSubscriptionBody) {
        subscriptionService.removeSubscription(removeSubscriptionBody);
        return ResponseEntity.ok().body("Email removido com sucesso");
    }
    
}
