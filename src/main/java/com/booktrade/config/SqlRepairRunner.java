package com.booktrade.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Runs AFTER DataSource is initialized.
 * Safely repairs Railway MySQL: adds missing columns, creates missing tables.
 * All operations are idempotent 鈥?safe to run on every startup.
 */
@Component
public class SqlRepairRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SqlRepairRunner.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        try (Connection conn = dataSource.getConnection()) {
            String driverName = conn.getMetaData().getDriverName();
            log.info("SqlRepairRunner: connected to 鈫?{}", driverName);

            // Only repair on MySQL (skip H2)
            if (driverName == null || !driverName.contains("MySQL")) {
                log.info("SqlRepairRunner: skipping (not MySQL: {})", driverName);
                return;
            }

            // 1. Ensure all tables exist
            log.info("SqlRepairRunner: creating tables if missing...");
            runScript(conn, "sql/railway-repair-create.sql");

            // 2. Add missing columns (deleted on user/book)
            addColumnIfMissing(conn, "user", "deleted");
            addColumnIfMissing(conn, "book", "deleted");

            log.info("鉁?SqlRepairRunner: Railway MySQL repair complete!");

        } catch (Exception e) {
            log.warn("SqlRepairRunner: repair skipped (DB not ready yet?) 鈫?{}", e.getMessage());
        }
    }

    private void addColumnIfMissing(Connection conn, String table, String column) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                 "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + table + "' AND COLUMN_NAME = '" + column + "'")) {
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` TINYINT NOT NULL DEFAULT 0");
                log.info("SqlRepairRunner: added `{}`.`{}` column", table, column);
            } else {
                log.info("SqlRepairRunner: `{}`.`{}` already exists, skipping", table, column);
            }
        } catch (Exception e) {
            log.warn("SqlRepairRunner: failed to check/add {}.{} 鈫?{}", table, column, e.getMessage());
        }
    }

    private void runScript(Connection conn, String scriptPath) {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource(scriptPath));
            populator.setContinueOnError(true);
            populator.populate(conn);
        } catch (Exception e) {
            log.warn("SqlRepairRunner: script {} failed 鈫?{}", scriptPath, e.getMessage());
        }
    }
}
