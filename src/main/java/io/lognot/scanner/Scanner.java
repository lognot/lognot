package io.lognot.scanner;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lognot.file.LogFile;
import io.lognot.notification.Notification;
import io.lognot.notification.Notifier;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;

public class Scanner implements Runnable {
    private static final Logger LOG = Logger.getLogger(Scanner.class);

    private Notifier notifier;

    private LogFile logFile;

    private long offset;

    @Autowired
    private ScannerStats scannerStats;

    public Scanner(LogFile file, Notifier notifier) {
        this.logFile = file;
        this.notifier = notifier;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(logFile.getKey());
        LOG.debug("File path: " + logFile.getPath());
        Notification.Builder notBuilder = new Notification.Builder();
        FileInputStream stream = null;
        java.util.Scanner scanner = null;
        try {

            File file = new File(logFile.getPath());
            stream = new FileInputStream(file);
            scanner = new java.util.Scanner(stream, "UTF-8");

            calculateOffsetIfLogFileRotated(file, stream);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.matches(logFile.getRegEx())) {
                    LOG.debug(String.format("Matched line in file '%s'. Line: '%s'.", logFile.getPath(), line));

                    notBuilder.withFile(logFile)
                            .withLine(line);

                }
                offset += line.length() + 1;
            }
//            TODO improve stats.
//            scannerStats.addFileMeta(logFile.getKey(), this.toJson());

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
            stream.skip(offset);
            LOG.debug(String.format("Skipped to offset %d.", offset));
        }
    }

    public long getOffset() {
        return offset;
    }

    public ScannerStats getScannerStats() {
        return scannerStats;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
