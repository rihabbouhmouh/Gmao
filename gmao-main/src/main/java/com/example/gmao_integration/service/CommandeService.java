package com.example.gmao_integration.service;

import com.example.gmao_integration.model.Commande;
import com.example.gmao_integration.repository.CommandeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public CommandeService(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    @Transactional
    public void importCommande(InputStream inputStream) throws IOException {
        List<Commande> commandes = new ArrayList<>();
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            // Lire l'en-tête
            String header = reader.readLine();
            lineNumber++;
            log.debug("En-tête du fichier: {}", header);

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = parseLine(line);
                    
                    if (parts.length != 8) {
                        log.warn("Ligne {} ignorée - nombre de colonnes incorrect ({} au lieu de 8): {}", 
                                lineNumber, parts.length, line);
                        errorCount++;
                        continue;
                    }

                    Commande commande = mapLineToCommande(parts);
                    commandes.add(commande);
                    successCount++;
                    
                    // Sauvegarde par lots pour optimiser les performances
                    if (commandes.size() >= 100) {
                        commandeRepository.saveAll(commandes);
                        commandes.clear();
                    }
                } catch (Exception e) {
                    log.error("Erreur ligne {}: {} - Cause: {}", lineNumber, line, e.getMessage());
                    errorCount++;
                }
            }
            
            // Sauvegarder les commandes restantes
            if (!commandes.isEmpty()) {
                commandeRepository.saveAll(commandes);
            }
            
            log.info("Import terminé - {} lignes traitées, {} succès, {} erreurs", 
                    (lineNumber-1), successCount, errorCount);
        }
    }

    private String[] parseLine(String line) {
        // Gérer les cas où la description contient des virgules
        String[] tempParts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        String[] parts = new String[8];
        
        for (int i = 0; i < tempParts.length && i < 8; i++) {
            parts[i] = tempParts[i].trim();
            // Supprimer les guillemets si présents
            if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) {
                parts[i] = parts[i].substring(1, parts[i].length() - 1);
            }
        }
        return parts;
    }

    private Commande mapLineToCommande(String[] parts) {
        Commande commande = new Commande();
        
        commande.setCodeProjet(parseString(parts[0]));
        commande.setNumCommande(parseString(parts[1]));
        commande.setDateCommande(parseDateTime(parts[2]));
        commande.setNumFournisseur(parseString(parts[3]));
        commande.setDescriptionCommande(parseString(parts[4]));
        commande.setDevise(parseString(parts[5]));
        commande.setMontantCommande(parseBigDecimal(parts[6]));
        commande.setMontantRecept(parseBigDecimal(parts[7]));
        
        return commande;
    }

    private String parseString(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return null;
        }
        return value.trim();
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Format de date invalide: {}", value);
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Gérer les séparateurs de milliers et les virgules décimales
            String cleanedValue = value.trim()
                                    .replaceAll("\\s", "")
                                    .replace(",", ".");
            return new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            log.error("Format numérique invalide: {}", value);
            return BigDecimal.ZERO;
        }
    }
}