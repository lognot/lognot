package io.lognot.notification;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

@Service
public class EmailNotifier implements Notifier {
    private static final Logger LOG = Logger.getLogger(EmailNotifier.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${lognot.notification.recipients}")
    private String notificationRecipients;

    @Override
    public void send(Notification notification) {
        if (StringUtils.isEmpty(notificationRecipients)) {
            LOG.info("Please set notification recipients to enable email notification.");
        } else {
            LOG.info("Sending email notification for " + notification.getFile().getPath());

            mailSender.send((mimeMessage) -> {
                mimeMessage.setFrom("no-reply@lognot.io");
                mimeMessage.setRecipients(Message.RecipientType.TO, notificationRecipients);
                mimeMessage.setText(notification.toString());
            });
        }
    }
}
