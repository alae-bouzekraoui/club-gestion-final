package ma.xproce.club_gestion.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeClub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private String objectifs;
    private LocalDateTime dateDemande = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private Utilisateur demandeur;

    private List<String> emailsMembres;
    private String statut;
    private String commentaireAdmin;


}