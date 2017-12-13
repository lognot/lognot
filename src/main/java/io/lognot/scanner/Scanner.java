package io.lognot.scanner;


import io.lognot.file.LogFile;
import io.lognot.notification.Notification;
import io.lognot.notification.Notifier;
import org.apache.log4j.Logger;

import java.io.*;

public class Scanner implements Runnable {
    private static final Logger LOG = Logger.getLogger(Scanner.class);

    public static final int LINE_SEPARATOR_LENGTH = System.getProperty("line.separator").getBytes().length;

    private Notifier notifier;

    private LogFile logFile;

    private long offset;

    public Scanner(LogFile file, Notifier notifier) {
        this.logFile = file;
        this.notifier = notifier;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(logFile.getKey());

        if (!logFile.exists()) {
            LOG.debug("Log file " + logFile.getActualPath() + " does not exist. It may have been deleted.");
            return;
        }
        LOG.debug("File path: " + logFile.getActualPath());

        Notification.Builder notBuilder = new Notification.Builder();

        FileInputStream stream = null;
        java.util.Scanner scanner = null;
        try {

            File file = new File(logFile.getActualPath());
            stream = new FileInputStream(file);
            scanner = new java.util.Scanner(stream, "UTF-8");

            calculateOffsetIfLogFileRotated(file, stream);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.matches(logFile.getRegEx())) {
                    LOG.debug(String.format("Matched line in file '%s'. Line: '%s'.", logFile.getActualPath(), line));

                    notBuilder.withFile(logFile)
                            .withLine(line);

                }
                offset += line.length() + LINE_SEPARATOR_LENGTH;
            }

            notBuilder.build().ifPresent(notifier::send);

        } catch (Exception e) {
            LOG.error("Failed to scan!", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOG.error("Failed to close file stream " + e);
                }
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private void calculateOffsetIfLogFileRotated(File file, FileInputStream stream) throws IOException {
        if (file.length() < offset ) {
            offset = 0;
        } else if (offset > 0) {
            offset = stream.skip(offset);
            LOG.debug(String.format("Skipped to offset %d.", offset));
        }
    }

    public long getOffset() {
        return offset;
    }
}
