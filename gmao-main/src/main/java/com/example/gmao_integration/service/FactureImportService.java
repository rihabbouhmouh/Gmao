package com.example.gmao_integration.service;

import com.example.gmao_integration.dto.FactureCsvDto;
import com.example.gmao_integration.model.Facture;
import com.example.gmao_integration.repository.FactureRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FactureImportService {

    private final FactureRepository factureRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FactureImportService(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    private String clean(String value) {
        return value != null ? value.trim().replaceAll("\\s+", " ") : null;
    }

    @PostConstruct
    public void init() {
        try {
            // Supprimer toutes les anciennes factures pour éviter les doublons
            factureRepository.deleteAll();
            System.out.println("✅ Suppression de toutes les anciennes factures");

            ClassPathResource resource = new ClassPathResource("Facture.csv");

            if (!resource.exists()) {
                System.err.println("❌ Fichier Facture.csv introuvable dans resources");
                return;
            }

            importerDepuisCSV(resource.getFile().getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Erreur init import: " + e.getMessage());
        }
    }

    public void importerDepuisCSV(String cheminFichier) {
        try {
            if (!Files.exists(Paths.get(cheminFichier))) {
                throw new FileNotFoundException("Fichier introuvable : " + cheminFichier);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cheminFichier), "UTF-8"))) {

                List<FactureCsvDto> facturesCSV = new CsvToBeanBuilder<FactureCsvDto>(reader)
                        .withType(FactureCsvDto.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .withIgnoreEmptyLine(true)
                        .build()
                        .parse();

                List<Facture> factures = new ArrayList<>();

                for (FactureCsvDto csv : facturesCSV) {
                    System.out.println("codeProjet brut : " + csv.getCodeProjet());
                    System.out.println("numFacture brut : " + csv.getNumFacture());

                    if (StringUtils.isBlank(clean(csv.getCodeProjet())) || StringUtils.isBlank(clean(csv.getNumFacture()))) {
                        System.err.println("⚠ Ligne ignorée - Champs obligatoires manquants");
                        continue;
                    }

                    Facture facture = new Facture();
                    facture.setCodeProjet(clean(csv.getCodeProjet()).split("\\s*,\\s*"));
                    facture.setNumFacture(clean(csv.getNumFacture()));

                    if (StringUtils.isNotBlank(csv.getDateFacture())) {
                        String datePart = csv.getDateFacture().split(" ")[0].trim();
                        facture.setDateFacture(LocalDate.parse(datePart, DATE_FORMATTER));
                    }

                    facture.setMontantFactureAP(
                            StringUtils.isNotBlank(csv.getMontantFactureAP()) ?
                                    Double.parseDouble(clean(csv.getMontantFactureAP())) : null);

                    facture.setCategorie(clean(csv.getCategorie()));
                    facture.setFournisseur(clean(csv.getFournisseur()));

                    factures.add(facture);
                }

                factureRepository.saveAll(factures);
                System.out.println("✅ Import terminé. " + factures.size() + " factures ajoutées.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur import CSV : " + e.getMessage(), e);
        }
    }
}
