package sn.ahmad.GestionCommandes.service.impl;

import sn.ahmad.GestionCommandes.dto.request.ClientRequest;
import sn.ahmad.GestionCommandes.dto.response.ClientResponse;
import sn.ahmad.GestionCommandes.entity.Client;
import sn.ahmad.GestionCommandes.exception.ConflictException;
import sn.ahmad.GestionCommandes.exception.ResourceNotFoundException;
import sn.ahmad.GestionCommandes.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ahmad.GestionCommandes.service.ClientService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public ClientResponse creer(ClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Un client avec cet email existe déjà");
        }

        Client client = Client.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .build();

        return toResponse(clientRepository.save(client));
    }

    @Override
    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        return toResponse(client);
    }

    @Override
    public List<ClientResponse> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClientResponse modifier(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        if (!client.getEmail().equals(request.getEmail())
                && clientRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Un client avec cet email existe déjà");
        }

        client.setNom(request.getNom());
        client.setEmail(request.getEmail());

        return toResponse(clientRepository.save(client));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client non trouvé");
        }
        clientRepository.deleteById(id);
    }

    private ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .nom(client.getNom())
                .email(client.getEmail())
                .username(client.getUtilisateur() != null
                        ? client.getUtilisateur().getUsername() : null)
                .build();
    }
}
