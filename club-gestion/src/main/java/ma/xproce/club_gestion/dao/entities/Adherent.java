package ma.xproce.club_gestion.dao.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Adherent extends Utilisateur{

    //relation adherent-evenement
    @ManyToMany(mappedBy = "participants")
    private List<Evenement> evenements = new ArrayList<>();


    //relation adherent-club
    @ManyToMany(mappedBy = "adherents", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Club> clubs = new ArrayList<>();
}
