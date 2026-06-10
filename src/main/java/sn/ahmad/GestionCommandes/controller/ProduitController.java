package sn.ahmad.GestionCommandes.controller;

import sn.ahmad.GestionCommandes.dto.request.ProduitRequest;
import sn.ahmad.GestionCommandes.dto.response.ProduitResponse;
import sn.ahmad.GestionCommandes.service.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits")
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    @Operation(summary = "Créer un produit",
            description = "Réservé aux ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produit créé"),
            @ApiResponse(responseCode = "409", description = "Nom déjà utilisé"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ProduitResponse> creer(@Valid @RequestBody ProduitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitService.creer(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un produit par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<ProduitResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les produits")
    public ResponseEntity<List<ProduitResponse>> findAll() {
        return ResponseEntity.ok(produitService.findAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un produit",
            description = "Réservé aux ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produit modifié"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "409", description = "Nom déjà utilisé")
    })
    public ResponseEntity<ProduitResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ProduitRequest request) {
        return ResponseEntity.ok(produitService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit",
            description = "Réservé aux ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produit supprimé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        produitService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}