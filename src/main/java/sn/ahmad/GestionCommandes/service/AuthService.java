package sn.ahmad.GestionCommandes.service;

import sn.ahmad.GestionCommandes.dto.request.LoginRequest;
import sn.ahmad.GestionCommandes.dto.request.RegisterRequest;
import sn.ahmad.GestionCommandes.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String token);
}
