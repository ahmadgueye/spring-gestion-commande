package sn.ahmad.GestionCommandes.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommandeRequest {

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    private List<LigneCommandeRequest> lignes;
}