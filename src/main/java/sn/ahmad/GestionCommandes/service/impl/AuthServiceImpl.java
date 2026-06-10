package sn.ahmad.GestionCommandes.service.impl;

import sn.ahmad.GestionCommandes.dto.request.LoginRequest;
import sn.ahmad.GestionCommandes.dto.request.RegisterRequest;
import sn.ahmad.GestionCommandes.dto.response.AuthResponse;
import sn.ahmad.GestionCommandes.entity.Role;
import sn.ahmad.GestionCommandes.entity.Token;
import sn.ahmad.GestionCommandes.entity.Utilisateur;
import sn.ahmad.GestionCommandes.exception.BusinessException;
import sn.ahmad.GestionCommandes.exception.ConflictException;
import sn.ahmad.GestionCommandes.repository.RoleRepository;
import sn.ahmad.GestionCommandes.repository.TokenRepository;
import sn.ahmad.GestionCommandes.repository.UtilisateurRepository;
import sn.ahmad.GestionCommandes.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (utilisateurRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Ce username est déjà utilisé");
        }
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Cet email est déjà utilisé");
        }

        // Par défaut tout nouvel utilisateur a le rôle USER
        Role roleUser = roleRepository.findByName(Role.RoleEnum.ROLE_USER)
                .orElseThrow(() -> new BusinessException("Rôle USER non trouvé en base"));

        Utilisateur utilisateur = Utilisateur.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(roleUser))
                .build();

        utilisateurRepository.save(utilisateur);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwt = jwtService.generateToken(userDetails);

        sauvegarderToken(jwt, utilisateur);

        return AuthResponse.builder()
                .token(jwt)
                .username(utilisateur.getUsername())
                .email(utilisateur.getEmail())
                .roles(List.of(roleUser.getName().name()))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        Utilisateur utilisateur = utilisateurRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        // Révoquer les anciens tokens
        revoquerTokens(utilisateur);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwt = jwtService.generateToken(userDetails);

        sauvegarderToken(jwt, utilisateur);

        List<String> roles = utilisateur.getRoles().stream()
                .map(r -> r.getName().name()).toList();

        return AuthResponse.builder()
                .token(jwt)
                .username(utilisateur.getUsername())
                .email(utilisateur.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            tokenRepository.save(t);
        });
    }

    private void sauvegarderToken(String jwt, Utilisateur utilisateur) {
        Token token = Token.builder()
                .token(jwt)
                .revoked(false)
                .expiredDate(new Date(System.currentTimeMillis() + 86400000))
                .utilisateur(utilisateur)
                .build();
        tokenRepository.save(token);
    }

    private void revoquerTokens(Utilisateur utilisateur) {
        List<Token> tokens = tokenRepository
                .findValidTokensByUtilisateur(utilisateur.getId());
        tokens.forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(tokens);
    }
}