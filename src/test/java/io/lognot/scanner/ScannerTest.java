package io.lognot.scanner;

import io.lognot.file.LogFile;
import io.lognot.notification.Notification;
import io.lognot.notification.Notifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScannerTest {

    @Autowired
    private ApplicationContext testContext;

    @Mock
    private Notifier notifierMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void emptyFileTest() {
        LogFile logFile = new LogFile("empty", "src/test/resources/logs/empty.log", ".*ERROR.*");

        Scanner scanner = testContext.getBean(Scanner.class, logFile, notifierMock);
        scanner.run();

        verify(notifierMock, never()).send(any(Notification.class));
        assertEquals("Offset should not change in empty file.", 0L, scanner.getOffset());

        ScannerStats scannerStats = scanner.getScannerStats();
        assertNotNull(scannerStats);
    }

    @Test
    public void offsetTest() {
        String path = "src/test/resources/logs/2.log";
        LogFile logFile = new LogFile("empty", path, ".*ERROR.*");

        Scanner scanner = testContext.getBean(Scanner.class, logFile, notifierMock);
        scanner.run();

        assertEquals("Offset must match to number of characters in file.",
                new File(path).length(),
                scanner.getOffset());
    }

    @Test
    public void foundPatternTest() throws IOException {
        String path = "src/test/resources/logs/1.log";
        LogFile logFile = new LogFile("1", path, ".*ERROR.*");

        Scanner scanner = testContext.getBean(Scanner.class, logFile, notifierMock);
        scanner.run();

        assertEquals("Offset must match to number of characters in file.",
                new File(path).length(),
                scanner.getOffset());

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notifierMock).send(captor.capture());

        Notification notification = captor.getValue();

        assertEquals("LogFile object not valid on notification.", logFile, notification.getFile());
        assertEquals("Number of lines does not match.", 2, notification.getLines().size());

//        TODO fix when working on stats.
//        ScannerStats scannerStats = scanner.getScannerStats();
//        assertNotNull(scannerStats);
//        assertEquals("Number of scanners does not match.", 1, scannerStats.getStats().size());
        // TODO add more
    }
}
