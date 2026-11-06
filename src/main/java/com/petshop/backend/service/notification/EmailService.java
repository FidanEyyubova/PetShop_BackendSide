package com.petshop.backend.service.notification;

import com.petshop.backend.config.AppProperties;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3";

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String host;
    private final String from;
    private final String fromName;
    private final String brevoApiKey;
    private final String brevoFrom;
    private final RestClient brevo = RestClient.create(BREVO_API_URL);

    public EmailService(ObjectProvider<JavaMailSender> mailSender,
                        @Value("${spring.mail.host:}") String host,
                        @Value("${spring.mail.username:}") String username,
                        AppProperties properties) {
        this.mailSender = mailSender;
        this.host = host;
        String configuredFrom = properties.getMail().getFrom();
        this.from = username != null && !username.isBlank() ? username.trim()
                : configuredFrom == null ? "" : configuredFrom.trim();
        this.fromName = properties.getMail().getFromName();
        this.brevoApiKey = properties.getMail().getBrevoApiKey();
        this.brevoFrom = configuredFrom == null ? "" : configuredFrom.trim();
    }

    private boolean isBrevoEnabled() {
        return brevoApiKey != null && !brevoApiKey.isBlank();
    }

    private boolean isSmtpEnabled() {
        return host != null && !host.isBlank() && mailSender.getIfAvailable() != null;
    }

    public boolean isEnabled() {
        return isBrevoEnabled() || isSmtpEnabled();
    }

    @Async
    public void send(String to, String subject, String body) {
        if (isBrevoEnabled()) {
            sendViaBrevo(to, subject, body);
        } else if (isSmtpEnabled()) {
            sendViaSmtp(to, subject, body);
        } else {
            log.warn("Email is not configured (BREVO_API_KEY or MAIL_HOST); email '{}' to {} was not sent", subject, to);
        }
    }

    private void sendViaBrevo(String to, String subject, String body) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", fromName, "email", brevoFrom),
                    "to", List.of(Map.of("email", to.trim())),
                    "replyTo", Map.of("email", brevoFrom),
                    "subject", subject,
                    "textContent", body,
                    "htmlContent", toHtml(subject, body));
            brevo.post()
                    .uri("/smtp/email")
                    .header("api-key", brevoApiKey.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Email '{}' sent to {} via Brevo", subject, to);
        } catch (Exception e) {
            log.error("Failed to send email '{}' to {} via Brevo: {}", subject, to, e.getMessage());
        }
    }

    private void sendViaSmtp(String to, String subject, String body) {
        try {
            JavaMailSender sender = mailSender.getObject();
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(from, fromName, StandardCharsets.UTF_8.name()));
            helper.setReplyTo(from);
            helper.setTo(to.trim());
            helper.setSubject(subject);
            helper.setText(body, toHtml(subject, body));
            sender.send(message);
            log.info("Email '{}' sent to {}", subject, to);
        } catch (Exception e) {
            log.error("Failed to send email '{}' to {}: {}", subject, to, e.getMessage());
        }
    }

    private String toHtml(String subject, String body) {
        String content = HtmlUtils.htmlEscape(body).replace("\n", "<br>");
        return """
                <!doctype html>
                <html><body style="margin:0;padding:24px;background:#f4f6f8;font-family:Arial,Helvetica,sans-serif;color:#1f2933">
                  <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:8px;padding:28px">
                    <h2 style="margin:0 0 16px;font-size:20px;color:#1f2933">%s</h2>
                    <div style="font-size:15px;line-height:1.6">%s</div>
                    <hr style="border:none;border-top:1px solid #e4e7eb;margin:24px 0 12px">
                    <div style="font-size:12px;color:#7b8794">%s &middot; This email was sent automatically.</div>
                  </div>
                </body></html>
                """.formatted(HtmlUtils.htmlEscape(subject), content, HtmlUtils.htmlEscape(fromName));
    }
}
