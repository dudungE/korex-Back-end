package com.project.korex.mail.service;

import com.project.korex.auth.service.AuthService;
import com.project.korex.user.enums.VerificationPurpose;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.lang.reflect.Method;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MailServiceTest {
    @Mock
    JavaMailSender mailSender;

    @Mock
    SpringTemplateEngine templateEngine;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage())
                .thenAnswer(inv -> new MimeMessage(Session.getInstance(new Properties())));
    }

    @Test
    @DisplayName("SIGN_UP 메일을 정상 구성하고 전송한다")
    void sendEmail_forSignUp_shouldBuildAndSendMail() throws Exception {
        when(mailSender.createMimeMessage())
                .thenAnswer(inv -> new MimeMessage(Session.getInstance(new Properties())));
        when(templateEngine.process(eq("email-verification"), any(Context.class)))
                .thenReturn("<html><body>CODE:<b>123456</b></body></html>");

        Class<?> purposeClass = Class.forName("com.project.korex.user.enums.VerificationPurpose");
        Object SIGN_UP = Enum.valueOf((Class<Enum>) purposeClass.asSubclass(Enum.class), "SIGN_UP");
        Method m = AuthService.class.getDeclaredMethod("sendEmail", String.class, String.class, purposeClass);
        m.setAccessible(true);

        m.invoke(authService, "user@example.com", "123456", SIGN_UP);

        ArgumentCaptor<Context> ctxCap = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("email-verification"), ctxCap.capture());
        assertThat(ctxCap.getValue().getVariable("code")).isEqualTo("123456");

        ArgumentCaptor<MimeMessage> msgCap = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(msgCap.capture());
        MimeMessage sent = msgCap.getValue();

        assertThat(sent.getSubject()).isEqualTo("[Korex] 이메일 인증 코드");
        assertThat(((InternetAddress) sent.getRecipients(Message.RecipientType.TO)[0]).getAddress())
                .isEqualTo("user@example.com");
        InternetAddress from = (InternetAddress) sent.getFrom()[0];
        assertThat(from.getAddress()).isEqualTo("ghyunjin0913@gmail.com");
        assertThat(from.getPersonal()).isEqualTo("Korex");
    }

    @Test
    @DisplayName("회원가입 인증이 아닌 경우(else 분기) 비밀번호 재설정 제목을 사용한다")
    void sendEmail_elseBranch_shouldUseResetPasswordSubject() throws Exception {
        // given
        String to = "user2@example.com";
        String code = "999999";
        when(templateEngine.process(eq("email-verification"), any(Context.class)))
                .thenReturn("<html>OK</html>");

        // when: purpose에 null을 주면 (== SIGN_UP) 비교가 false → else 분기
        Method m = AuthService.class.getDeclaredMethod("sendEmail", String.class, String.class, VerificationPurpose.class);
        m.setAccessible(true);
        m.invoke(authService, to, code, null);

        // then
        ArgumentCaptor<MimeMessage> msgCap = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(msgCap.capture());
        MimeMessage sent = msgCap.getValue();
        assertThat(sent.getSubject()).isEqualTo("[Korex] 비밀번호 재설정 인증 코드");
        assertThat(((InternetAddress) sent.getRecipients(MimeMessage.RecipientType.TO)[0]).getAddress())
                .isEqualTo(to);
    }

    @Test
    @DisplayName("이메일 전송 실패 시 런타임 예외로 전파한다")
    void sendEmail_sendFailed_shouldThrowRuntime() throws Exception {
        // given
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html>...</html>");

        doThrow(new MailSendException("메일 전송 실패")).when(mailSender).send(any(MimeMessage.class));

        Method m = AuthService.class.getDeclaredMethod("sendEmail", String.class, String.class, VerificationPurpose.class);
        m.setAccessible(true);

        // when & then
        assertThatThrownBy(() -> m.invoke(authService, "user@example.com", "123456", VerificationPurpose.SIGN_UP))
                .hasCauseInstanceOf(MailSendException.class)
                .cause()
                .hasMessageContaining("메일 전송 실패");
    }

    // --- helper: multipart/단일 본문 모두 대응 ---
    private static String extractHtml(MimeMessage msg) throws Exception {
        Object content = msg.getContent();
        if (content instanceof MimeMultipart mp) {
            return (String) mp.getBodyPart(0).getContent();
        }
        return content.toString();
    }
}
