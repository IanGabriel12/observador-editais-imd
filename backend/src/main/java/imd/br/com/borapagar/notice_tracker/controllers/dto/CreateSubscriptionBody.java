package imd.br.com.borapagar.notice_tracker.controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateSubscriptionBody(@NotNull @Email @NotBlank String email) {};
