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
import java.sql.Statement;

@Component
public class SqlRepairRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SqlRepairRunner.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        log.info("SqlRepairRunner starting...");
        try (Connection conn = dataSource.getConnection()) {
            String driverName = conn.getMetaData().getDriverName();
            log.info("SqlRepairRunner: driver = {}", driverName);

            if (driverName == null || !driverName.toLowerCase().contains("mysql")) {
                log.info("SqlRepairRunner: not MySQL, skipping");
                return;
            }

            // Step 1: Create all missing tables (idempotent CREATE TABLE IF NOT EXISTS)
            log.info("SqlRepairRunner: creating tables if missing...");
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                    new ClassPathResource("sql/railway-repair-create.sql"));
                populator.setContinueOnError(true);
                populator.setIgnoreFailedDrops(true);
                populator.populate(conn);
                log.info("SqlRepairRunner: table creation script executed");
            } catch (Exception e) {
                log.warn("SqlRepairRunner: table creation had errors (may be ok): {}", e.getMessage());
            }

            // Step 2: Add missing `deleted` columns
            tryAddColumn(conn, "user", "deleted", "TINYINT NOT NULL DEFAULT 0");
            tryAddColumn(conn, "book", "deleted", "TINYINT NOT NULL DEFAULT 0");

            // Step 3: Fix notification table AUTO_INCREMENT (critical for broadcast)
            fixNotificationAutoIncrement(conn);

            log.info("SqlRepairRunner: all repairs complete!");

        } catch (Exception e) {
            log.error("SqlRepairRunner: fatal error: {}", e.getMessage());
        }
    }

    private void tryAddColumn(Connection conn, String table, String column, String definition) {
        String sql = "ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("SqlRepairRunner: SUCCESS - added `{}`.`{}`", table, column);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("duplicate column")) {
                log.info("SqlRepairRunner: `{}`.`{}` already exists, ok", table, column);
            } else {
                log.warn("SqlRepairRunner: `{}`.`{}` - {}", table, column,
                    msg != null ? msg.substring(0, Math.min(100, msg.length())) : "unknown");
            }
        }
    }

    private void fixNotificationAutoIncrement(Connection conn) {
        try {
            String checkSql = "SELECT column_type FROM information_schema.columns " +
                    "WHERE table_schema = DATABASE() AND table_name = 'notification' AND column_name = 'id'";
            try (Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(checkSql)) {
                if (rs.next()) {
                    String columnType = rs.getString("column_type");
                    if (columnType != null && !columnType.toUpperCase().contains("AUTO_INCREMENT")) {
                        log.info("SqlRepairRunner: notification.id missing AUTO_INCREMENT, fixing...");
                        String alterSql = "ALTER TABLE `notification` MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT";
                        try (Statement alterStmt = conn.createStatement()) {
                            alterStmt.execute(alterSql);
                            log.info("SqlRepairRunner: SUCCESS - notification.id now has AUTO_INCREMENT");
                        }
                    } else {
                        log.info("SqlRepairRunner: notification.id already has AUTO_INCREMENT");
                    }
                } else {
                    log.warn("SqlRepairRunner: notification table or id column not found");
                }
            }
        } catch (Exception e) {
            log.warn("SqlRepairRunner: fixNotificationAutoIncrement failed: {}", e.getMessage());
        }
    }
}
