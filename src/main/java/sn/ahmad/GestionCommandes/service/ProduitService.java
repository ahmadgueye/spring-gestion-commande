package sn.ahmad.GestionCommandes.service;


import sn.ahmad.GestionCommandes.dto.request.ProduitRequest;
import sn.ahmad.GestionCommandes.dto.response.ProduitResponse;
import java.util.List;

public interface ProduitService {
    ProduitResponse creer(ProduitRequest request);
    ProduitResponse findById(Long id);
    List<ProduitResponse> findAll();
    ProduitResponse modifier(Long id, ProduitRequest request);
    void supprimer(Long id);
}