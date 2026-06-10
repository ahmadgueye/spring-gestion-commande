package sn.ahmad.GestionCommandes.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommandeResponse {

    private Long id;
    private LocalDateTime dateCommande;
    private String status;
    private String clientNom;
    private List<LigneCommandeResponse> lignes;
    private java.math.BigDecimal total;
}