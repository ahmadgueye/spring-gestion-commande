package sn.ahmad.GestionCommandes.controller;

import sn.ahmad.GestionCommandes.dto.request.CommandeRequest;
import sn.ahmad.GestionCommandes.dto.response.CommandeResponse;
import sn.ahmad.GestionCommandes.service.CommandeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "Gestion des commandes")
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    @Operation(summary = "Créer une commande")
    public ResponseEntity<CommandeResponse> creer(@Valid @RequestBody CommandeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.creer(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une commande par ID")
    public ResponseEntity<CommandeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les commandes")
    public ResponseEntity<List<CommandeResponse>> findAll() {
        return ResponseEntity.ok(commandeService.findAll());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Commandes d'un client")
    public ResponseEntity<List<CommandeResponse>> findByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(commandeService.findByClientId(clientId));
    }

    @GetMapping("/entre-dates")
    @Operation(summary = "Commandes entre deux dates")
    public ResponseEntity<List<CommandeResponse>> findEntreDeuxDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(commandeService.findEntreDeuxDates(debut, fin));
    }

    @PatchMapping("/{id}/valider")
    @Operation(summary = "Valider une commande")
    public ResponseEntity<CommandeResponse> valider(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.valider(id));
    }

    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler une commande")
    public ResponseEntity<CommandeResponse> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.annuler(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commande")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        commandeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistiques/chiffre-affaires")
    @Operation(summary = "Chiffre d'affaires global")
    public ResponseEntity<BigDecimal> chiffreAffaires() {
        return ResponseEntity.ok(commandeService.chiffreAffairesGlobal());
    }
}
