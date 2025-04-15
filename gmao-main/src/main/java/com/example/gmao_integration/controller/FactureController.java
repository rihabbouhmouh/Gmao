package com.example.gmao_integration.controller;

import com.example.gmao_integration.model.Facture;
import com.example.gmao_integration.repository.FactureRepository;
import com.example.gmao_integration.service.FactureImportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/factures")
public class FactureController {

    private final FactureImportService importService;
    private final FactureRepository repository;
    private static final String UPLOAD_DIR = "uploads/";

    public FactureController(FactureImportService importService, FactureRepository repository) {
        this.importService = importService;
        this.repository = repository;
        createUploadDir();
    }

    private void createUploadDir() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier uploads", e);
        }
    }

    @PostMapping
    public ResponseEntity<Facture> create(@RequestBody Facture facture) {
        if (repository.existsByNumFacture(facture.getNumFacture())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(facture));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getById(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Facture>> getAll(Pageable pageable) {
        return ResponseEntity.ok(repository.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Facture> update(@PathVariable Long id, @RequestBody Facture facture) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        facture.setId(id);
        return ResponseEntity.ok(repository.save(facture));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import/upload")
    public ResponseEntity<String> importFromUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichier vide");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (!filename.toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Le fichier doit être un CSV");
        }

        try {
            String newFilename = UUID.randomUUID() + "_" + filename;
            Path destination = Paths.get(UPLOAD_DIR).resolve(newFilename);

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            importService.importerDepuisCSV(destination.toString());

            return ResponseEntity.ok("Upload et import réussi : " + filename);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }
}
