package sn.ahmad.GestionCommandes.service;

import sn.ahmad.GestionCommandes.dto.request.CommandeRequest;
import sn.ahmad.GestionCommandes.dto.response.CommandeResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CommandeService {
    CommandeResponse creer(CommandeRequest request);
    CommandeResponse findById(Long id);
    List<CommandeResponse> findAll();
    List<CommandeResponse> findByClientId(Long clientId);
    List<CommandeResponse> findEntreDeuxDates(LocalDateTime debut, LocalDateTime fin);
    CommandeResponse valider(Long id);
    CommandeResponse annuler(Long id);
    void supprimer(Long id);
    BigDecimal chiffreAffairesGlobal();
}