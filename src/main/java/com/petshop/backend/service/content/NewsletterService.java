package com.petshop.backend.service.content;

import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.service.notification.EmailService;
import com.petshop.backend.dto.response.content.NewsletterSubscriberResponse;
import com.petshop.backend.entity.marketing.NewsletterSubscriber;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.repository.marketing.NewsletterSubscriberRepository;
import com.petshop.backend.util.PageUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NewsletterService {

    private final NewsletterSubscriberRepository repository;
    private final EmailService emailService;
    private final AppProperties properties;

    public NewsletterService(NewsletterSubscriberRepository repository, EmailService emailService, AppProperties properties) {
        this.repository = repository;
        this.emailService = emailService;
        this.properties = properties;
    }

    /** 409 when the email is already subscribed; a previously unsubscribed email is re-activated. */
    @Transactional
    public NewsletterSubscriberResponse subscribe(String rawEmail) {
        String email = rawEmail.trim().toLowerCase();
        NewsletterSubscriber subscriber = repository.findByEmailIgnoreCase(email).orElse(null);
        if (subscriber != null && Boolean.TRUE.equals(subscriber.getActive())) {
            throw new ConflictException("ALREADY_SUBSCRIBED", "This email is already subscribed");
        }
        if (subscriber == null) {
            subscriber = NewsletterSubscriber.builder().email(email).active(true).build();
        } else {
            subscriber.setActive(true);
            subscriber.setUnsubscribedAt(null);
        }
        NewsletterSubscriber saved = repository.save(subscriber);

        // Welcome email to the subscriber.
        emailService.send(email, "Welcome to the PetShop newsletter",
                "Hello!\n\nThank you for subscribing to the PetShop newsletter. "
                        + "You will receive news about new products, offers and pet-care tips.\n\n"
                        + "Visit us: " + properties.getFrontendUrl() + "\n\nPetShop team");
        // Notification to the shop's own inbox.
        String notifyTo = properties.getMail().getNotifyTo();
        if (notifyTo != null && !notifyTo.isBlank()) {
            emailService.send(notifyTo.trim(), "New newsletter subscriber",
                    "A new subscriber joined the newsletter: " + email);
        }
        return toResponse(saved);
    }

    /** Always succeeds (does not reveal whether the email was subscribed). */
    @Transactional
    public void unsubscribe(String rawEmail) {
        repository.findByEmailIgnoreCase(rawEmail.trim()).ifPresent(s -> {
            s.setActive(false);
            s.setUnsubscribedAt(LocalDateTime.now());
            repository.save(s);
        });
    }

    @Transactional(readOnly = true)
    public PageResponse<NewsletterSubscriberResponse> list(int page, int size) {
        return PageResponse.of(repository.findAll(PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))),
                NewsletterService::toResponse);
    }

    private static NewsletterSubscriberResponse toResponse(NewsletterSubscriber s) {
        return NewsletterSubscriberResponse.builder()
                .id(s.getId()).email(s.getEmail()).active(s.getActive())
                .createdAt(s.getCreatedAt()).unsubscribedAt(s.getUnsubscribedAt()).build();
    }
}
