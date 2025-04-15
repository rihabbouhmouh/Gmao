package com.example.gmao_integration.dto;

import com.opencsv.bean.CsvBindByName;

public class FactureCsvDto {

    @CsvBindByName(column = "code_projet")
    private String codeProjet;

    @CsvBindByName(column = "num_facture")
    private String numFacture;

    @CsvBindByName(column = "date_facture")
    private String dateFacture;

    @CsvBindByName(column = "montant_facture_ap")
    private String montantFactureAP;

    @CsvBindByName(column = "categorie")
    private String categorie;

    @CsvBindByName(column = "fournisseur")
    private String fournisseur;

    // Getters & Setters
    public String getCodeProjet() { return codeProjet; }
    public void setCodeProjet(String codeProjet) { this.codeProjet = codeProjet; }

    public String getNumFacture() { return numFacture; }
    public void setNumFacture(String numFacture) { this.numFacture = numFacture; }

    public String getDateFacture() { return dateFacture; }
    public void setDateFacture(String dateFacture) { this.dateFacture = dateFacture; }

    public String getMontantFactureAP() { return montantFactureAP; }
    public void setMontantFactureAP(String montantFactureAP) { this.montantFactureAP = montantFactureAP; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }
}
