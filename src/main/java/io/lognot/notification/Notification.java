package io.lognot.notification;

import io.lognot.file.LogFile;

import java.util.*;

public class Notification {
    private LogFile file;

    private List<String> lines;

    private Notification(Builder builder) {
        setFile(builder.file);
        setLines(builder.lines);
    }

    public LogFile getFile() {
        return file;
    }

    public void setFile(LogFile file) {
        this.file = file;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public static class Builder {
        private LogFile file;

        private List<String> lines = new ArrayList<>();

        public Builder withFile(LogFile file) {
            this.file = file;
            return this;
        }

        public Builder withLines(List<String> lines) {
            this.lines = lines;
            return this;
        }

        public Builder withLine(String line) {
            this.lines.add(line);
            return this;
        }

        public Optional<Notification> build() {
            if (file != null && !lines.isEmpty()) {
                return Optional.of(new Notification(this));
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public String toString() {
        return "Notification{" +
                "file=" + file +
                ", lines=" + lines +
                '}';
    }
}
