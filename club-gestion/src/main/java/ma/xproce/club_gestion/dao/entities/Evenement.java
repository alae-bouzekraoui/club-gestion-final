package ma.xproce.club_gestion.dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    private LocalDate date;
    private String lieu;
    private String etat;

    //relation adherent-evenement
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Adherent> participants = new ArrayList<>();

    //relation club-evenement
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    //relation membreBureau-evenement
    @ManyToOne
    @JoinColumn(name = "membre_bureau_id")
    private MembreBureau membreBureau;

}
