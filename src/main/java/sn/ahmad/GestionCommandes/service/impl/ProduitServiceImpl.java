package sn.ahmad.GestionCommandes.service.impl;

import sn.ahmad.GestionCommandes.dto.request.ProduitRequest;
import sn.ahmad.GestionCommandes.dto.response.ProduitResponse;
import sn.ahmad.GestionCommandes.entity.Produit;
import sn.ahmad.GestionCommandes.entity.Utilisateur;
import sn.ahmad.GestionCommandes.exception.ResourceNotFoundException;
import sn.ahmad.GestionCommandes.exception.ConflictException;
import sn.ahmad.GestionCommandes.repository.ProduitRepository;
import sn.ahmad.GestionCommandes.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ahmad.GestionCommandes.service.ProduitService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public ProduitResponse creer(ProduitRequest request) {
        if (produitRepository.existsByNom(request.getNom())) {
            throw new ConflictException("Un produit avec ce nom existe déjà");
        }

        // Récupérer l'utilisateur connecté comme créateur
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Utilisateur createur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Produit produit = Produit.builder()
                .nom(request.getNom())
                .prix(request.getPrix())
                .stock(request.getStock())
                .createur(createur)
                .build();

        return toResponse(produitRepository.save(produit));
    }

    @Override
    public ProduitResponse findById(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        return toResponse(produit);
    }

    @Override
    public List<ProduitResponse> findAll() {
        return produitRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProduitResponse modifier(Long id, ProduitRequest request) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        // Vérifier unicité du nom si changé
        if (!produit.getNom().equals(request.getNom())
                && produitRepository.existsByNom(request.getNom())) {
            throw new ConflictException("Un produit avec ce nom existe déjà");
        }

        produit.setNom(request.getNom());
        produit.setPrix(request.getPrix());
        produit.setStock(request.getStock());

        return toResponse(produitRepository.save(produit));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit non trouvé");
        }
        produitRepository.deleteById(id);
    }

    private ProduitResponse toResponse(Produit produit) {
        return ProduitResponse.builder()
                .id(produit.getId())
                .nom(produit.getNom())
                .prix(produit.getPrix())
                .stock(produit.getStock())
                .createurUsername(produit.getCreateur() != null
                        ? produit.getCreateur().getUsername() : null)
                .build();
    }
}
