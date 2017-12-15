package io.lognot;

import io.lognot.notification.Notifier;
import io.lognot.scanner.FilePathResolver;
import io.lognot.scanner.Scanner;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "io.lognot")
@Import(LognotConfig.class)
public class LognotApplication implements CommandLineRunner {
	private static final Logger LOG = Logger.getLogger(LognotApplication.class);
	
	public static final long DEFAULT_PERIOD = 60L;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private LognotConfig lognotConfig;

	@Autowired
	private Notifier notifier;

	@Autowired
	private FilePathResolver filePathResolver;

	public static void main(String[] args) {
		SpringApplication.run(LognotApplication.class, args);
	}

	public void run(String... args) {
		Assert.notEmpty(lognotConfig.getFiles(), "Empty list of log files to scan. Check your configuration file.");

		int noOfFiles = lognotConfig.getFiles().size();
		LOG.debug("Files: " + noOfFiles);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(noOfFiles);
		lognotConfig.getFiles().forEach(file -> {
			file.setFileResolver(filePathResolver);
			if (file.exists()) {
				Scanner scanner = applicationContext.getBean(Scanner.class, file, notifier);
				long period = Optional.ofNullable(file.getPeriod()).orElse(DEFAULT_PERIOD);
				executor.scheduleAtFixedRate(scanner, 0, period, TimeUnit.SECONDS);
			} else {
				LOG.error("File " + file.getPath() + " cannot be traced. Check if it is readable.");
			}
		});
	}
}
