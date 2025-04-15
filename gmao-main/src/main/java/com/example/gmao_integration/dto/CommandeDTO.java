package com.example.gmao_integration.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeDTO {
    private String codeProjet;
    private String numCommande;
    private String dateCommande;
    private String numFournisseur;
    private String descriptionCommande;
    private String devise;
    private String montantCommande;
    private String montantRecept;
}
