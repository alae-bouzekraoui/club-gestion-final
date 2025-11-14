package ma.xproce.club_gestion.dao.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@PrimaryKeyJoinColumn(name = "id")
public class MembreBureau extends Utilisateur{
    private String poste;


    @ManyToMany(mappedBy = "membreBureauList",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Club> clubList;

    @ManyToMany(mappedBy = "membreBureauAdheranList",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Club> clubListAdherent;

    //relation membreBureau-realisation
    @OneToMany(mappedBy = "membreBureau", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Realisation> realisationList;

    //relation membreBureau-evenement
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Evenement> evenementOrganises;

}
