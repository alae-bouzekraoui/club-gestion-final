package ma.xproce.club_gestion.dao.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembreBureau extends Utilisateur{
    private String poste;

    //relation club-membreBureau
    @ManyToMany(mappedBy = "membreBureauList",fetch = FetchType.EAGER)
    private List<Club> clubList;

    //relation membreBureau-realisation
    @OneToMany(mappedBy = "membreBureau", fetch = FetchType.EAGER)
    private List<Realisation> realisationList;

    //relation membreBureau-evenement
    @OneToMany(mappedBy = "membreBureau", fetch = FetchType.EAGER)
    private List<Evenement> evenementOrganises;

}
