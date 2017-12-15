package io.lognot.file;

import io.lognot.scanner.FilePathResolver;

import java.io.File;

public class LogFile {

    private String key;

    private String path;

    private String regEx;

    private Long period;

    private FilePathResolver fileResolver;

    public LogFile() {
    }

    public LogFile(String key, String path, String regEx) {
        this.key = key;
        this.path = path;
        this.regEx = regEx;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegEx() {
        return regEx;
    }

    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public void setFileResolver(FilePathResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    public String getActualPath() {
        if (fileResolver != null) {
            return fileResolver.resolve(path);
        } else {
            return path;
        }
    }

    public boolean exists() {
        return new File(getActualPath()).exists();
    }

    @Override
    public String toString() {
        return "LogFile{" +
                "key='" + key + '\'' +
                ", path='" + path + '\'' +
                ", regEx='" + regEx + '\'' +
                ", period=" + period +
                '}';
    }
}
