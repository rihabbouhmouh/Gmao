package com.example.gmao_integration.repository;

import com.example.gmao_integration.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
