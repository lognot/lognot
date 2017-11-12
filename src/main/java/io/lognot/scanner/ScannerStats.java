package io.lognot.scanner;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ManagedResource
@Component
public class ScannerStats {
    private int numberOfScanners;

    private Map<String, String> stats = new HashMap<>();

    @ManagedAttribute
    public int getNumberOfScanners() {
        return numberOfScanners;
    }

    public void incrementNumberOfScanners() {
        this.numberOfScanners++;
    }

    public void addFileMeta(String key, String fileMeta) {
        stats.put(key, fileMeta);
    }

    @ManagedAttribute
    public Map<String, String> getStats() {
        return stats;
    }
}
