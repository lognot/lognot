package io.lognot;

import io.lognot.file.LogFile;
import io.lognot.notification.Notifier;
import io.lognot.scanner.Scanner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templatemode.TemplateModeHandler;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
}
