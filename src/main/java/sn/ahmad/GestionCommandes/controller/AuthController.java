package sn.ahmad.GestionCommandes.controller;

import sn.ahmad.GestionCommandes.dto.request.LoginRequest;
import sn.ahmad.GestionCommandes.dto.request.RegisterRequest;
import sn.ahmad.GestionCommandes.dto.response.AuthResponse;
import sn.ahmad.GestionCommandes.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription, connexion, déconnexion")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Créer un compte",
            description = "Crée un compte avec le rôle USER par défaut")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte créé, token retourné"),
            @ApiResponse(responseCode = "409", description = "Username ou email déjà utilisé")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter",
            description = "Retourne un token JWT à utiliser dans Authorization: Bearer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Se déconnecter",
            description = "Révoque le token JWT courant")
    @ApiResponse(responseCode = "204", description = "Déconnexion réussie")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return ResponseEntity.noContent().build();
    }
}