package sn.ahmad.GestionCommandes.config;

import sn.ahmad.GestionCommandes.entity.*;
import sn.ahmad.GestionCommandes.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // -----------------------------------------------
        // 1. Rôles
        // -----------------------------------------------
        Role roleAdmin = roleRepository.findByName(Role.RoleEnum.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(Role.RoleEnum.ROLE_ADMIN).build()));

        Role roleUser = roleRepository.findByName(Role.RoleEnum.ROLE_USER)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(Role.RoleEnum.ROLE_USER).build()));

        log.info("✅ Rôles initialisés");

        // -----------------------------------------------
        // 2. Utilisateurs
        // -----------------------------------------------
        Utilisateur admin = utilisateurRepository.findByUsername("admin")
                .orElseGet(() -> {
                    Utilisateur u = Utilisateur.builder()
                            .username("admin")
                            .email("admin@polytech.com")
                            .password(passwordEncoder.encode("admin123"))
                            .enabled(true)
                            .roles(Set.of(roleAdmin))
                            .build();
                    return utilisateurRepository.save(u);
                });

        Utilisateur userAli = utilisateurRepository.findByUsername("ali")
                .orElseGet(() -> {
                    Utilisateur u = Utilisateur.builder()
                            .username("ali")
                            .email("ali@gmail.com")
                            .password(passwordEncoder.encode("ali123"))
                            .enabled(true)
                            .roles(Set.of(roleUser))
                            .build();
                    return utilisateurRepository.save(u);
                });

        Utilisateur userFatou = utilisateurRepository.findByUsername("fatou")
                .orElseGet(() -> {
                    Utilisateur u = Utilisateur.builder()
                            .username("fatou")
                            .email("fatou@gmail.com")
                            .password(passwordEncoder.encode("fatou123"))
                            .enabled(true)
                            .roles(Set.of(roleUser))
                            .build();
                    return utilisateurRepository.save(u);
                });

        log.info("✅ Utilisateurs initialisés");

        // -----------------------------------------------
        // 3. Clients
        // -----------------------------------------------
        Client clientAli = clientRepository.findByEmail("ali@gmail.com")
                .orElseGet(() -> {
                    Client c = Client.builder()
                            .nom("Ali Diallo")
                            .email("ali@gmail.com")
                            .utilisateur(userAli)
                            .build();
                    return clientRepository.save(c);
                });

        Client clientFatou = clientRepository.findByEmail("fatou@gmail.com")
                .orElseGet(() -> {
                    Client c = Client.builder()
                            .nom("Fatou Ndiaye")
                            .email("fatou@gmail.com")
                            .utilisateur(userFatou)
                            .build();
                    return clientRepository.save(c);
                });

        log.info("✅ Clients initialisés");

        // -----------------------------------------------
        // 4. Produits
        // -----------------------------------------------
        Produit laptop = produitRepository.findByNom("Laptop Dell")
                .orElseGet(() -> produitRepository.save(Produit.builder()
                        .nom("Laptop Dell")
                        .prix(new BigDecimal("999.99"))
                        .stock(50)
                        .createur(admin)
                        .build()));

        Produit souris = produitRepository.findByNom("Souris Logitech")
                .orElseGet(() -> produitRepository.save(Produit.builder()
                        .nom("Souris Logitech")
                        .prix(new BigDecimal("29.99"))
                        .stock(100)
                        .createur(admin)
                        .build()));

        Produit clavier = produitRepository.findByNom("Clavier Mécanique")
                .orElseGet(() -> produitRepository.save(Produit.builder()
                        .nom("Clavier Mécanique")
                        .prix(new BigDecimal("79.99"))
                        .stock(75)
                        .createur(admin)
                        .build()));

        Produit ecran = produitRepository.findByNom("Écran 27 pouces")
                .orElseGet(() -> produitRepository.save(Produit.builder()
                        .nom("Écran 27 pouces")
                        .prix(new BigDecimal("349.99"))
                        .stock(30)
                        .createur(admin)
                        .build()));

        log.info("✅ Produits initialisés");

        // -----------------------------------------------
        // 5. Commande complète
        // -----------------------------------------------
        if (commandeRepository.findByClientId(clientAli.getId()).isEmpty()) {

            Commande commande = Commande.builder()
                    .dateCommande(LocalDateTime.now())
                    .status(Commande.StatusCommande.CREATED)
                    .client(clientAli)
                    .lignes(new ArrayList<>())
                    .build();

            LigneCommande ligne1 = LigneCommande.builder()
                    .commande(commande)
                    .produit(laptop)
                    .quantite(1)
                    .prixUnitaire(laptop.getPrix())
                    .build();

            LigneCommande ligne2 = LigneCommande.builder()
                    .commande(commande)
                    .produit(souris)
                    .quantite(2)
                    .prixUnitaire(souris.getPrix())
                    .build();

            commande.getLignes().add(ligne1);
            commande.getLignes().add(ligne2);

            commandeRepository.save(commande);
            log.info("✅ Commande de test créée pour {}", clientAli.getNom());
        }

        log.info("🚀 Initialisation des données terminée");
    }
}