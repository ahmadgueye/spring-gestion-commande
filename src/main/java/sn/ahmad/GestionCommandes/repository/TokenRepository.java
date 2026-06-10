package sn.ahmad.GestionCommandes.repository;

import sn.ahmad.GestionCommandes.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    @Query("SELECT t FROM Token t WHERE t.utilisateur.id = :utilisateurId " +
            "AND t.revoked = false AND t.expiredDate > CURRENT_TIMESTAMP")
    List<Token> findValidTokensByUtilisateur(Long utilisateurId);
}
