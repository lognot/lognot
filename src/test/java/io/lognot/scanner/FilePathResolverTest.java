package io.lognot.scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FilePathResolverTest {

    private static final String DATE_FORMAT = "yyyyMMdd";

    private static final String DATE_FORMAT_ddMMyyyy = "dd/MM/yyyy";

    private static final String DATE_FORMAT_DASH = "yyyy-MM-dd";

    private static final String DATE_FORMAT_yyyyMM = "yyyyMM";

    private final String message;

    private final String expectedActualPath;

    private final String filePath;

    private FilePathResolver resolver = new FilePathResolver();

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][] {
                {
                    "File path without pattern!",
                        "/var/logs/app/console.log",
                        "/var/logs/app/console.log"
                },
                {
                    "File path with invalid pattern %s!",
                        "/var/logs/app/console-.log",
                        "/var/logs/app/console-%d{}.log"

                },
                {
                    String.format("File path with pattern %s!", DATE_FORMAT),
                        String.format("/var/logs/app/console-%s.log", new SimpleDateFormat(DATE_FORMAT).format(new Date())),
                        "/var/logs/app/console-%d{" + DATE_FORMAT + "}.log"
                }
                ,
                {
                    "File path containing pattern more than once!",
                        String.format("/var/logs/app/console-%s-%s.log",
                                new SimpleDateFormat(DATE_FORMAT).format(new Date()),
                                new SimpleDateFormat(DATE_FORMAT).format(new Date())),

                        "/var/logs/app/console-%d{" + DATE_FORMAT + "}-%d{" + DATE_FORMAT + "}.log"
                },
                {
                    String.format("File path with pattern %s!", DATE_FORMAT_DASH),
                        String.format("/var/logs/app/console-%s.log", new SimpleDateFormat(DATE_FORMAT_DASH).format(new Date())),
                        "/var/logs/app/console-%d{" + DATE_FORMAT_DASH + "}.log"
                },
                {
                    String.format("File path with pattern %s!", DATE_FORMAT_ddMMyyyy),
                        String.format("/var/logs/app/console-%s.log", new SimpleDateFormat(DATE_FORMAT_ddMMyyyy).format(new Date())),
                        "/var/logs/app/console-%d{" + DATE_FORMAT_ddMMyyyy + "}.log"
                },
                {
                    String.format("File path with pattern %s!", DATE_FORMAT_yyyyMM),
                        String.format("/var/logs/app/console-%s.log", new SimpleDateFormat(DATE_FORMAT_yyyyMM).format(new Date())),
                        "/var/logs/app/console-%d{" + DATE_FORMAT_yyyyMM + "}.log"

                }
        });
    }

    public FilePathResolverTest(String message, String expectedActualPath, String filePath) {
        this.message = message;
        this.expectedActualPath = expectedActualPath;
        this.filePath = filePath;
    }

    @Test
    public void testResolve() throws Exception {
        assertEquals(message, expectedActualPath, resolver.resolve(filePath));
    }
}