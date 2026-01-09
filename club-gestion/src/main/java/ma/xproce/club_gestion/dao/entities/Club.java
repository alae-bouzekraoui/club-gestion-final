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
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "adherent_club",
            joinColumns = @JoinColumn(name = "club_id"),
            inverseJoinColumns = @JoinColumn(name = "adherent_id")
    )
    @ToString.Exclude
    private List<Adherent> adherents = new ArrayList<>();

    //relation club-membreBureau
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<MembreBureau> membreBureauList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<MembreBureau> membreBureauAdheranList = new ArrayList<>();

    @OneToMany(mappedBy = "club",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evenement> evenementList = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Realisation> realisationList = new ArrayList<>();

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private List<DemandeAdhesion> demandeAdhesionList = new ArrayList<>();
}
