package com.booktrade.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SqlAutoRepairRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SqlAutoRepairRunner.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("=== Auto-repair: executing railway-repair.sql ===");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                    new ClassPathResource("sql/railway-repair.sql"));
            populator.setContinueOnError(true);
            populator.setSeparator(";");
            populator.execute(dataSource);
            log.info("=== railway-repair.sql executed successfully ===");
        } catch (Exception e) {
            log.error("railway-repair.sql execution failed: {}", e.getMessage());
        }
    }
}