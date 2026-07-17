package com.hotel.service;

import com.hotel.database.FileDatabase;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackupService {
    private final FileDatabase db;

    public BackupService(FileDatabase db) {
        this.db = db;
    }

    public boolean performManualBackup() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        db.log("Manual backup requested.");
        return db.backupData(timestamp);
    }

    public boolean performAutoBackup() {
        if ("true".equalsIgnoreCase(db.getSettings().getOrDefault("autoBackup", "true"))) {
            db.log("Automated backup trigger.");
            return db.backupData("auto_latest");
        }
        return false;
    }

    public boolean restoreFromBackup(String timestamp) {
        db.log("Data restore requested for backup: " + timestamp);
        return db.restoreData(timestamp);
    }

    public List<String> getAvailableBackups() {
        List<String> list = new ArrayList<>();
        File backupDir = new File("data/backup");
        if (!backupDir.exists()) return list;

        File[] files = backupDir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().startsWith("rooms_") && f.getName().endsWith(".txt")) {
                    // Extract the suffix
                    String name = f.getName();
                    String suffix = name.substring("rooms_".length(), name.length() - ".txt".length());
                    list.add(suffix);
                }
            }
        }
        return list;
    }
}
