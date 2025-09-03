package com.project.korex.alert.service;

import com.project.korex.alert.entity.AlertSetting;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAlertService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@korex.com}")
    private String fromEmail;

    /**
     * HTML 알람 이메일 발송
     */
    public boolean sendAlert(AlertSetting alert, BigDecimal currentRate) {
        try {
            log.info("Preparing to send HTML alert email - Alert ID: {}, Recipient: {}, Currency: {}",
                    alert.getId(), alert.getUserEmail(), alert.getCurrencyCode());

            MimeMessage message = createHtmlAlertMessage(alert, currentRate);
            mailSender.send(message);

            log.info("HTML Alert email sent successfully - Alert ID: {}, Recipient: {}, Currency: {}",
                    alert.getId(), alert.getUserEmail(), alert.getCurrencyCode());

            System.out.println("===========================================================");
            return true;

        } catch (Exception e) {
            log.error("Failed to send HTML alert email - Alert ID: {}, Recipient: {}, Error: {}",
                    alert.getId(), alert.getUserEmail(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * HTML 알람 메시지 생성
     */
    private MimeMessage createHtmlAlertMessage(AlertSetting alert, BigDecimal currentRate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(alert.getUserEmail());
        helper.setSubject(String.format("[KOREX] %s 환율 알림 📈", alert.getCurrencyCode()));

        // Thymeleaf 컨텍스트 생성
        Context context = new Context();
        context.setVariable("userName", alert.getUserName() != null ? alert.getUserName() : "고객");
        context.setVariable("currencyCode", alert.getCurrencyCode());
        context.setVariable("currentRate", currentRate);
        context.setVariable("targetRate", alert.getTargetRate());
        context.setVariable("conditionText", alert.getCondition().getDescription());
        context.setVariable("alertTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")));

        // HTML 템플릿 처리
        String htmlContent = templateEngine.process("alert-email", context);
        helper.setText(htmlContent, true);

        return message;
    }

    /**
     * 기존 텍스트 이메일 발송 (백업용)
     */
    public boolean sendTextAlert(AlertSetting alert, BigDecimal currentRate) {
        try {
            log.info("Preparing to send text alert email - Alert ID: {}, Recipient: {}, Currency: {}",
                    alert.getId(), alert.getUserEmail(), alert.getCurrencyCode());

            SimpleMailMessage message = createAlertMessage(alert, currentRate);
            System.out.println(message);
            mailSender.send(message);

            log.info("Text Alert email sent successfully - Alert ID: {}, Recipient: {}, Currency: {}",
                    alert.getId(), alert.getUserEmail(), alert.getCurrencyCode());

            System.out.println("===========================================================");
            return true;

        } catch (Exception e) {
            log.error("Failed to send text alert email - Alert ID: {}, Recipient: {}, Error: {}",
                    alert.getId(), alert.getUserEmail(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 기존 텍스트 알람 메시지 생성 (백업용)
     */
    private SimpleMailMessage createAlertMessage(AlertSetting alert, BigDecimal currentRate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(alert.getUserEmail());
        message.setSubject(String.format("[KOREX] %s Exchange Rate Alert 📈", alert.getCurrencyCode()));

        String conditionText = alert.getCondition().getDescription();
        String userName = alert.getUserName() != null ? alert.getUserName() : "Valued Customer";

        String body = String.format(
                "Hello %s!\n\n" +
                        "Your exchange rate alert has been triggered! 💰\n\n" +
                        "📊 Alert Details:\n" +
                        "• Currency: %s\n" +
                        "• Current Rate: %,.2f KRW\n" +
                        "• Target Rate: %,.2f KRW\n" +
                        "• Condition: %,.2f KRW %s\n" +
                        "• Alert Time: %s\n\n" +
                        "Check real-time exchange rates and start trading on KOREX app now!\n\n" +
                        "Thank you.\n" +
                        "Best regards,\n" +
                        "KOREX Team",
                userName,
                alert.getCurrencyCode(),
                currentRate.doubleValue(),
                alert.getTargetRate().doubleValue(),
                alert.getTargetRate().doubleValue(),
                conditionText,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        );

        message.setText(body);
        return message;
    }

    /**
     * HTML 테스트 이메일 발송
     */
    public boolean sendTestAlert(String recipientEmail, String currencyCode) {
        try {
            log.debug("Sending HTML test alert email to: {}", recipientEmail);

            MimeMessage message = createHtmlTestMessage(recipientEmail, currencyCode);
            mailSender.send(message);

            log.info("HTML Test alert email sent successfully to: {}", recipientEmail);
            return true;

        } catch (Exception e) {
            log.error("Failed to send HTML test alert email to: {}, Error: {}", recipientEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * HTML 테스트 메시지 생성
     */
    private MimeMessage createHtmlTestMessage(String recipientEmail, String currencyCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(recipientEmail);
        helper.setSubject("[KOREX] 환율 알림 테스트 📧");

        // 테스트용 Thymeleaf 컨텍스트 생성
        Context context = new Context();
        context.setVariable("userName", "테스트 사용자");
        context.setVariable("currencyCode", currencyCode);
        context.setVariable("currentRate", new BigDecimal("1350.50"));
        context.setVariable("targetRate", new BigDecimal("1300.00"));
        context.setVariable("conditionText", "이하");
        context.setVariable("alertTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")));

        // HTML 템플릿 처리
        String htmlContent = templateEngine.process("alert-email", context);
        helper.setText(htmlContent, true);

        return message;
    }

    /**
     * 기존 텍스트 테스트 이메일 발송 (백업용)
     */
    public boolean sendTextTestAlert(String recipientEmail, String currencyCode) {
        try {
            log.debug("Sending text test alert email to: {}", recipientEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("[KOREX] Exchange Rate Alert Test 📧");
            message.setText(createTestBody(currencyCode));

            mailSender.send(message);

            log.info("Text Test alert email sent successfully to: {}", recipientEmail);
            return true;

        } catch (Exception e) {
            log.error("Failed to send text test alert email to: {}, Error: {}", recipientEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 기존 텍스트 테스트 이메일 본문 생성 (백업용)
     */
    private String createTestBody(String currencyCode) {
        return String.format(
                "Hello!\n\n" +
                        "This is a test email for KOREX exchange rate alerts.\n\n" +
                        "• Test Currency: %s\n" +
                        "• Test Time: %s\n\n" +
                        "Your email alerts have been successfully configured! ✅\n\n" +
                        "Best regards,\n" +
                        "KOREX Team",
                currencyCode,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        );
    }
}
