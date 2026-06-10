package sn.ahmad.GestionCommandes.repository;

import sn.ahmad.GestionCommandes.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByClientId(Long clientId);

    List<Commande> findByDateCommandeBetween(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT SUM(l.quantite * l.prixUnitaire) FROM LigneCommande l " +
            "WHERE l.commande.status = 'VALIDATED'")
    BigDecimal calculerChiffreAffairesGlobal();

    @Query("SELECT c.client.id, SUM(l.quantite * l.prixUnitaire) " +
            "FROM Commande c JOIN c.lignes l " +
            "WHERE c.status = 'VALIDATED' " +
            "GROUP BY c.client.id")
    List<Object[]> totalParClient();
}
