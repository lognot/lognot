package io.lognot.notification;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailNotifier implements Notifier {
    private static final Logger LOG = Logger.getLogger(EmailNotifier.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${lognot.notification.recipients}")
    private String notificationRecipients;

    @Override
    public void send(Notification notification) {
        if (StringUtils.isEmpty(notificationRecipients)) {
            LOG.info("Please set notification recipients to enable email notification.");
        } else {
            LOG.info("Sending email notification for " + notification.getFile().getPath());

            mailSender.send((mimeMessage) -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                        StandardCharsets.UTF_8.name());
                helper.setSubject(String.format("Lognot notification for file %s", notification.getFile().getKey()));
                helper.setFrom("no-reply@lognot.io");
                helper.setTo(notificationRecipients);
                helper.setText(getHTMLBody(notification), true);
            });
        }
    }

    private String getHTMLBody(Notification notification) {
        Context context = new Context();
        context.setVariable("lognot_file_key", notification.getFile().getKey());
        context.setVariable("lognot_file_path", notification.getFile().getPath());
        context.setVariable("lognot_file_lines", notification.getLines());

        return templateEngine.process("notification", context);
    }

    public void setNotificationRecipients(String notificationRecipients) {
        this.notificationRecipients = notificationRecipients;
    }
}
