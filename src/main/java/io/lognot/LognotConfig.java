package io.lognot;

import io.lognot.file.LogFile;
import io.lognot.notification.Notifier;
import io.lognot.scanner.Scanner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties
public class LognotConfig {

    private List<LogFile> files = new ArrayList<>();

    public List<LogFile> getFiles() {
        return files;
    }

    public void setFiles(List<LogFile> files) {
        this.files = files;
    }

    @Bean
    @Scope("prototype")
    public Scanner scanner(LogFile file, Notifier notifier) {
        return new Scanner(file, notifier);
    }

    @Bean
    @Scope("prototype")
    public LogFile logFile() {
        return new LogFile();
    }


}
