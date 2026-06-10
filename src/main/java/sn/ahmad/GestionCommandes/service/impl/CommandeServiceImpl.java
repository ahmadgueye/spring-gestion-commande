package sn.ahmad.GestionCommandes.service.impl;

import sn.ahmad.GestionCommandes.dto.request.CommandeRequest;
import sn.ahmad.GestionCommandes.dto.request.LigneCommandeRequest;
import sn.ahmad.GestionCommandes.dto.response.CommandeResponse;
import sn.ahmad.GestionCommandes.dto.response.LigneCommandeResponse;
import sn.ahmad.GestionCommandes.entity.*;
import sn.ahmad.GestionCommandes.exception.BusinessException;
import sn.ahmad.GestionCommandes.exception.ResourceNotFoundException;
import sn.ahmad.GestionCommandes.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ahmad.GestionCommandes.service.CommandeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;

    @Override
    @Transactional
    public CommandeResponse creer(CommandeRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        Commande commande = Commande.builder()
                .dateCommande(LocalDateTime.now())
                .status(Commande.StatusCommande.CREATED)
                .client(client)
                .lignes(new ArrayList<>())
                .build();

        for (LigneCommandeRequest ligneRequest : request.getLignes()) {
            Produit produit = produitRepository.findById(ligneRequest.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

            LigneCommande ligne = LigneCommande.builder()
                    .commande(commande)
                    .produit(produit)
                    .quantite(ligneRequest.getQuantite())
                    .prixUnitaire(produit.getPrix())
                    .build();

            commande.getLignes().add(ligne);
        }

        return toResponse(commandeRepository.save(commande));
    }

    @Override
    public CommandeResponse findById(Long id) {
        return toResponse(commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée")));
    }

    @Override
    public List<CommandeResponse> findAll() {
        return commandeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<CommandeResponse> findByClientId(Long clientId) {
        return commandeRepository.findByClientId(clientId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<CommandeResponse> findEntreDeuxDates(LocalDateTime debut, LocalDateTime fin) {
        return commandeRepository.findByDateCommandeBetween(debut, fin)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public CommandeResponse valider(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (commande.getStatus() != Commande.StatusCommande.CREATED) {
            throw new BusinessException("Seule une commande CREATED peut être validée");
        }

        // Vérifier et déduire le stock
        for (LigneCommande ligne : commande.getLignes()) {
            Produit produit = ligne.getProduit();
            if (produit.getStock() < ligne.getQuantite()) {
                throw new BusinessException("Stock insuffisant pour le produit : "
                        + produit.getNom());
            }
            produit.setStock(produit.getStock() - ligne.getQuantite());
            produitRepository.save(produit);
        }

        commande.setStatus(Commande.StatusCommande.VALIDATED);
        return toResponse(commandeRepository.save(commande));
    }

    @Override
    @Transactional
    public CommandeResponse annuler(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (commande.getStatus() == Commande.StatusCommande.VALIDATED) {
            throw new BusinessException("Une commande VALIDATED ne peut pas être annulée");
        }
        if (commande.getStatus() == Commande.StatusCommande.CANCELLED) {
            throw new BusinessException("La commande est déjà annulée");
        }

        commande.setStatus(Commande.StatusCommande.CANCELLED);
        return toResponse(commandeRepository.save(commande));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (commande.getStatus() == Commande.StatusCommande.VALIDATED) {
            throw new BusinessException("Impossible de supprimer une commande VALIDATED");
        }

        commandeRepository.deleteById(id);
    }

    @Override
    public BigDecimal chiffreAffairesGlobal() {
        BigDecimal total = commandeRepository.calculerChiffreAffairesGlobal();
        return total != null ? total : BigDecimal.ZERO;
    }

    // --- Mapping ---

    private CommandeResponse toResponse(Commande commande) {
        List<LigneCommandeResponse> lignes = commande.getLignes()
                .stream()
                .map(this::toLigneResponse)
                .toList();

        BigDecimal total = lignes.stream()
                .map(LigneCommandeResponse::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CommandeResponse.builder()
                .id(commande.getId())
                .dateCommande(commande.getDateCommande())
                .status(commande.getStatus().name())
                .clientNom(commande.getClient().getNom())
                .lignes(lignes)
                .total(total)
                .build();
    }

    private LigneCommandeResponse toLigneResponse(LigneCommande ligne) {
        BigDecimal sousTotal = ligne.getPrixUnitaire()
                .multiply(BigDecimal.valueOf(ligne.getQuantite()));

        return LigneCommandeResponse.builder()
                .id(ligne.getId())
                .produitNom(ligne.getProduit().getNom())
                .quantite(ligne.getQuantite())
                .prixUnitaire(ligne.getPrixUnitaire())
                .sousTotal(sousTotal)
                .build();
    }
}
