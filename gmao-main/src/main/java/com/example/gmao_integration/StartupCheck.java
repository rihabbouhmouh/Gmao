package com.example.gmao_integration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class StartupCheck implements CommandLineRunner {
    
    private final DataSource dataSource;

    public StartupCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("✅ Connexion PostgreSQL réussie !");
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion à PostgreSQL");
            e.printStackTrace();
            System.exit(1);
        }
    }
}