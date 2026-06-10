package sn.ahmad.GestionCommandes.repository;


import sn.ahmad.GestionCommandes.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long>,
        JpaSpecificationExecutor<Produit> {

    Optional<Produit> findByNom(String nom);
    boolean existsByNom(String nom);
    List<Produit> findByPrixBetweenAndStockGreaterThanEqual(
            BigDecimal minPrix, BigDecimal maxPrix, Integer stockMin);
}