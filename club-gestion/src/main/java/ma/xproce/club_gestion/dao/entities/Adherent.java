package ma.xproce.club_gestion.dao.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id") // ✅ héritage propre de la clé primaire

public class Adherent extends Utilisateur{

    //relation adherent-evenement
    @ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
    private List<Evenement> evenements = new ArrayList<>();

    //relation adherent-club
    @ManyToMany(mappedBy = "adherents", fetch = FetchType.EAGER)
    private List<Club> clubs = new ArrayList<>();
}
