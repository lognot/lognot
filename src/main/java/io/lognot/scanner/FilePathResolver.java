package io.lognot.scanner;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FilePathResolver {

    private static final Logger LOG = Logger.getLogger(FilePathResolver.class);

    public String resolve(String path) {

        try {
            Pattern pattern = Pattern.compile(".*(%d\\{([y|M|d|\\-|/]*)\\}).*");
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                String datePattern = matcher.group(1);  // containing pattern marker, for example %d{yyyyMMdd}
                String dateFormat = matcher.group(2);   // for the example above yyyyMMdd
                DateFormat format = new SimpleDateFormat(dateFormat);
                datePattern = datePattern.replaceAll("\\{", "\\\\{")
                        .replaceAll("\\}", "\\\\}")
                        .replaceAll("\\-", "\\\\-");

                return path.replaceAll(datePattern, format.format(new Date()));
            }
        } catch (Exception e) {
            LOG.error("Failed to resolve path ", e);
        }

        return path;
    }
}
