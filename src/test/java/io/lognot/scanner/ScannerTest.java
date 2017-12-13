package io.lognot.scanner;

import io.lognot.file.LogFile;
import io.lognot.notification.Notification;
import io.lognot.notification.Notifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScannerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Autowired
    private ApplicationContext testContext;

    @Mock
    private Notifier notifierMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWithEmptyFile() {
        LogFile logFile = new LogFile("empty", "src/test/resources/logs/empty.log", ".*ERROR.*");

        Scanner scanner = testContext.getBean(Scanner.class, logFile, notifierMock);
        scanner.run();

        verify(notifierMock, never()).send(any(Notification.class));
        assertEquals("Offset should not change in empty file.", 0L, scanner.getOffset());
    }

    @Test
    public void testOffsetMatchNumberOfCharactersInFile() {
        String path = "src/test/resources/logs/2.log";
        LogFile logFile = new LogFile("empty", path, ".*ERROR.*");

        Scanner scanner = testContext.getBean(Scanner.class, logFile, notifierMock);
        scanner.run();

        assertEquals("Offset must match to number of characters in file.",
                new File(path).length(),
                scanner.getOffset());
    }

    @Test
    public void testPatternMatches() throws IOException {
        String path = "src/test/resources/logs/1.log";
        LogFile logFile = new LogFile("1", path, ".*ERROR.*|.*LognotApplication.*");

        Scanner scanner = testContext.getBean(Scanner.class, logFile, notifierMock);
        scanner.run();

        assertEquals("Offset must match to number of characters in file.",
                new File(path).length(),
                scanner.getOffset());

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notifierMock).send(captor.capture());

        Notification notification = captor.getValue();

        assertEquals("LogFile object not valid on notification.", logFile, notification.getFile());
        assertEquals("Number of lines does not match.", 5, notification.getLines().size());
    }

    @Test
    public void testLogFilenameContainingDatePattern() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        String actualLogFilePath = String.format("%s/console-%s.log", testFolder.getRoot(), format.format(new Date()));

        String logFilePath = testFolder.getRoot() + "/console-%d{yyyyMMdd}.log";

        Files.copy(Paths.get("src/test/resources/logs/1.log"), Paths.get(actualLogFilePath));

        LogFile logFile = new LogFile("file-containing-current-date", logFilePath, ".*ERROR.*|.*LognotApplication.*");
        logFile.setFileResolver(testContext.getBean(FilePathResolver.class));

        Scanner scanner = testContext.getBean(Scanner.class, logFile, notifierMock);
        scanner.run();

        assertEquals("Offset must match to number of characters in file.",
                new File(actualLogFilePath).length(),
                scanner.getOffset());

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notifierMock).send(captor.capture());

        Notification notification = captor.getValue();

        assertEquals("LogFile object not valid on notification.", logFile, notification.getFile());
        assertEquals("Number of lines does not match.", 5, notification.getLines().size());
    }
}
