package com.example.gmao_integration.repository;

import com.example.gmao_integration.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    @Query("SELECT COALESCE(SUM(f.montantFactureAP), 0) FROM Facture f")
    Double findTotalAmount();

    boolean existsByNumFacture(String numFacture);
}
