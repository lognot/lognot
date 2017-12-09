package io.lognot.notification;

import io.lognot.file.LogFile;
import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;

public class NotificationTest {

    public static final String PATH = "/var/log/apache/console.log";

    @Test
    public void shouldCreateNotification() {
        Notification notification = new Notification.Builder()
                .withFile(new LogFile("1", PATH, ".*ERROR.*"))
                .withLine("line 1")
                .withLine("line 2")
                .withLines(Arrays.asList("line 3", "line 4"))
                .withLine("line 5")
                .build().get();

        assertEquals("File key does not match!", "1", notification.getFile().getKey());
        assertEquals("Path does not match!", PATH, notification.getFile().getPath());
        assertEquals("Line count does not match", 5, notification.getLines().size());

        for (int i = 0; i < 5; i++) {
            assertEquals("Line order does not match!", "line " + (i + 1), notification.getLines().get(i));
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailToCreateNotificationWithoutFileAndLines() {
        new Notification.Builder().build().get();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailToCreateNotificationWithoutFile() {
        new Notification.Builder()
                .withLine("one line")
                .build().get();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailToCreateNotificationWithoutLines() {
        new Notification.Builder()
                .withFile(new LogFile("1", PATH, ".*ERROR.*"))
                .build().get();
    }
}