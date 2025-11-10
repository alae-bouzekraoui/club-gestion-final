package ma.xproce.club_gestion.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String description;
    private String objectifs;
    private String dateCreation;

    //relation adherent-club
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "adherent_club",
            joinColumns = @JoinColumn(name = "club_id"),
            inverseJoinColumns = @JoinColumn(name = "adherent_id")
    )
    private List<Adherent> adherents = new ArrayList<>();

    //relation club-membreBureau
    @ManyToMany(fetch = FetchType.EAGER)
    private List<MembreBureau> membreBureauList = new ArrayList<>();

    //relation club-evenement
    @OneToMany(mappedBy = "club",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Evenement> evenementList = new ArrayList<>();

    //relation club-realisation
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Realisation> realisationList = new ArrayList<>();

}
