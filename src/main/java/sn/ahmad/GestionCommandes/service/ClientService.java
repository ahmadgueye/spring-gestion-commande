package sn.ahmad.GestionCommandes.service;

import sn.ahmad.GestionCommandes.dto.request.ClientRequest;
import sn.ahmad.GestionCommandes.dto.response.ClientResponse;
import java.util.List;

public interface ClientService {
    ClientResponse creer(ClientRequest request);
    ClientResponse findById(Long id);
    List<ClientResponse> findAll();
    ClientResponse modifier(Long id, ClientRequest request);
    void supprimer(Long id);
}