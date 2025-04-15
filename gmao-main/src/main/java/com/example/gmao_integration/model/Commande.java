package com.example.gmao_integration.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codeProjet;

    private String numCommande;

    private LocalDateTime dateCommande;

    private String numFournisseur;

    @Column(columnDefinition = "text")
    private String descriptionCommande;

    private String devise;

    private BigDecimal montantCommande;

    private BigDecimal montantRecept;
}
