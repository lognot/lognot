package io.lognot;

import io.lognot.file.LogFile;
import io.lognot.notification.Notifier;
import io.lognot.scanner.Scanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties
public class LognotConfig {

    @Value("${lognot.notification.recipients}")
    private String notificationRecipients;

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

    @Bean
    public TemplateEngine springTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }


    @Bean
    public ITemplateResolver htmlTemplateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(StandardTemplateModeHandlers.HTML5.getTemplateModeName());
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return templateResolver;
    }

    @Bean String [] notificationRecipients() {
        String [] recipients = {};
        if (!StringUtils.isEmpty(notificationRecipients)) {
            recipients = notificationRecipients.split(",");
        }
        return recipients;
    }
}
