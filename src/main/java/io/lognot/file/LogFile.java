package io.lognot.file;

import java.io.File;

public class LogFile {

    private String key;

    private String path;

    private String regEx;

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

    public boolean exists() {
        return new File(path).exists();
    }

    @Override
    public String toString() {
        return "{" +
                "'key':'" + key + '\'' +
                ", 'path':'" + path + '\'' +
                ", 'regEx':'" + regEx + '\'' +
                '}';
    }
}
