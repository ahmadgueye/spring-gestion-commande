package sn.ahmad.GestionCommandes.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClientResponse {

    private Long id;
    private String nom;
    private String email;
    private String username;
}
