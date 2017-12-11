package io.lognot.notification;

import io.lognot.file.LogFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailNotifierTest {

    @InjectMocks
    private EmailNotifier victim;

    @Mock
    private JavaMailSender mailSenderMock;

    private Notification notification;

    @Before
    public void setUp() throws Exception {
        notification = new Notification.Builder()
                .withFile(new LogFile("1", "src/test/resources/logs/1.log", ".*ERROR.*"))
                .withLine("2017-11-12 08:54:04.003 ERROR 3700 --- [           main] io.lognot.LognotApplication              : File /tmp/2 cannot be traced. Check if it is readable.")
                .build().get();

        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = RuntimeException.class)
    public void testSendFails() throws Exception {
        victim.setNotificationRecipients(new String [] { "john.doe@example.com", "richard.moe@mail.com" });

        doThrow(new RuntimeException("failed")).when(mailSenderMock).send(any(MimeMessagePreparator.class));

        victim.send(notification);
    }

    @Test
    public void shouldNotTryToSendIfRecipientsAreNotSet() {
        victim.send(notification);
        verify(mailSenderMock, never()).send(any(MimeMessagePreparator.class));
    }
}