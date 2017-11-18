package io.lognot;

import io.lognot.notification.Notifier;
import io.lognot.scanner.Scanner;
import io.lognot.scanner.ScannerStats;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAutoConfiguration
@Import(LognotConfig.class)
@ComponentScan(basePackages = "io.lognot")
public class LognotApplication implements CommandLineRunner {
	private static final Logger LOG = Logger.getLogger(LognotApplication.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private LognotConfig lognotConfig;

	@Autowired
	private ScannerStats scannerStats;

	@Autowired
	private Notifier notifier;

	public static void main(String[] args) {
		SpringApplication.run(LognotApplication.class, args);
	}

	public void run(String... args) {
		Assert.notEmpty(lognotConfig.getFiles(), "Empty list of log files to scan. Check your configuration file.");

		int noOfFiles = lognotConfig.getFiles().size();
		LOG.debug("Files: " + noOfFiles);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(noOfFiles);
		lognotConfig.getFiles().forEach((file) -> {
			if (file.exists()) {
				Scanner scanner = applicationContext.getBean(Scanner.class, file, notifier);
				executor.scheduleAtFixedRate(scanner, 0, 30, TimeUnit.SECONDS);
				scannerStats.incrementNumberOfScanners();
			} else {
				LOG.error("File " + file.getPath() + " cannot be traced. Check if it is readable.");
			}
		});
	}
}
