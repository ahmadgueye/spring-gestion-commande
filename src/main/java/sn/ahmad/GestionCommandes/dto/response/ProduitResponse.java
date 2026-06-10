package sn.ahmad.GestionCommandes.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProduitResponse {

    private Long id;
    private String nom;
    private BigDecimal prix;
    private Integer stock;
    private String createurUsername;
}