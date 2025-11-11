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
public class MembreBureau extends Utilisateur{
    private String poste;


    @ManyToMany(mappedBy = "membreBureauList",fetch = FetchType.LAZY)
    private List<Club> clubList;

    //relation membreBureau-realisation
    @OneToMany(mappedBy = "membreBureau", fetch = FetchType.EAGER)
    private List<Realisation> realisationList;

    //relation membreBureau-evenement
    @OneToMany(mappedBy = "membreBureau", fetch = FetchType.EAGER)
    private List<Evenement> evenementOrganises;

}
