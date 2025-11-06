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

    @ManyToMany(mappedBy = "membreBureau",fetch = FetchType.EAGER)
    private List<Club> Club;

    @OneToMany(mappedBy = "club", fetch = FetchType.EAGER)
    private List<Realisation> realisationList;

}
