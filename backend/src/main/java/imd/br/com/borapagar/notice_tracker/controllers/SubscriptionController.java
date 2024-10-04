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

    /**
     * Inscreve um email para receber os novos editais aparecerem
     * @param subscriptionDTO - Corpo da requisição. Contém o email
     * @return Resposta indicando o resultado da inscrição
     */
    @PostMapping
    public ResponseEntity<String> subscribeEmail(@RequestBody @Valid CreateSubscriptionBody subscriptionDTO) {
        subscriptionService.insertNewSubscription(subscriptionDTO);
        return ResponseEntity.ok().body("Email registrado com sucesso");
    }

    /**
     * Faz com que o email inscrito pare de receber novas mensagens.
     * 
     * <p> Para previnir que uma pessoa que não tenha acesso ao email realize essa ação, 
     * em vez do email, este método recebe um token. Este token foi gerado para o usuário no momento da inscrição. 
     * O token está presente em todas as mensagens recebidas.
     * @param removeSubscriptionBody
     * @return
     */
    @DeleteMapping
    public ResponseEntity<String> removeSubscription(@RequestBody @Valid RemoveSubscriptionBody removeSubscriptionBody) {
        subscriptionService.removeSubscription(removeSubscriptionBody);
        return ResponseEntity.ok().body("Email removido com sucesso");
    }
    
}
