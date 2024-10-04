package imd.br.com.borapagar.notice_tracker.helpers;

import lombok.Builder;

@Builder
public record MailRequest(String to, String subject, String content) {
    
}
