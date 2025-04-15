package com.example.gmao_integration.controller;

import com.example.gmao_integration.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/commande")
public class CommandeController {

    @Autowired
    private CommandeService commandeService;

    @PostMapping("/import")
    public ResponseEntity<String> importCommande(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier est vide");
        }
        
        try {
            commandeService.importCommande(file.getInputStream());
            return ResponseEntity.ok("Importation des commandes réussie !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'import: " + e.getMessage());
        }
    }
}
