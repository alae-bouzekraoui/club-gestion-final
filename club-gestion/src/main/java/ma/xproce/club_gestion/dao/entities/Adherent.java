package ma.xproce.club_gestion.dao.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Adherent extends Utilisateur{

    @ManyToMany(mappedBy = "adherentList", fetch = FetchType.EAGER)
    private List<Club> clubList;

    @ManyToMany(mappedBy = "adherentList", fetch = FetchType.EAGER)
    private List<Evenement> evenementList;

}
