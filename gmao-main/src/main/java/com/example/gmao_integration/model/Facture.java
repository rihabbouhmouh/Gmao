package com.example.gmao_integration.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_projet")
    private String[] codeProjet;

    @Column(name = "num_facture")
    private String numFacture;

    @Column(name = "date_facture")
    private LocalDate dateFacture;

    @Column(name = "montant_facture_ap")
    private Double montantFactureAP;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "fournisseur")
    private String fournisseur;

    public Facture() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String[] getCodeProjet() { return codeProjet; }
    public void setCodeProjet(String[] codeProjet) { this.codeProjet = codeProjet; }

    public String getNumFacture() { return numFacture; }
    public void setNumFacture(String numFacture) { this.numFacture = numFacture; }

    public LocalDate getDateFacture() { return dateFacture; }
    public void setDateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; }

    public Double getMontantFactureAP() { return montantFactureAP; }
    public void setMontantFactureAP(Double montantFactureAP) { this.montantFactureAP = montantFactureAP; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }
}
