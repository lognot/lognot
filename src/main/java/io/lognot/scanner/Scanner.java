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
        LOG.debug("File path: " + logFile.getPath());
        try {
            File file = new File(logFile.getPath());
            RandomAccessFile reader = new RandomAccessFile(logFile.getPath(), "r");

            calculateOffsetIfLogFileRotated(file, reader);

            Notification.Builder notBuilder = new Notification.Builder();
            String line;
            while ((line = reader.readLine()) != null) {
                offset += line.length() + 1;
                if (line.matches(logFile.getRegEx())) {
                    LOG.debug(String.format("Matched line in file '%s'. Line: '%s'.", logFile.getPath(), line));

                    notBuilder.withFile(logFile)
                            .withLine(line);

                }

                scannerStats.addFileMeta(logFile.getKey(), this.toJson());
            }

            notBuilder.build().ifPresent(notifier::send);

            reader.close();
        } catch (Exception e) {
            LOG.error("Failed to scan!", e);
        }
    }

    private void calculateOffsetIfLogFileRotated(File file, RandomAccessFile reader) throws IOException {
        if (file.length() < offset ) {
            offset = 0;
        } else if (offset > 0) {
            reader.seek(offset);
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
