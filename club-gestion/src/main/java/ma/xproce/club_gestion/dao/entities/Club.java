package ma.xproce.club_gestion.dao.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
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
    private Date dateCreation;

    @ManyToMany(mappedBy = "clubs",fetch = FetchType.EAGER)
    private List<Evenement> evenementList;

    @ManyToMany(mappedBy = "club",fetch = FetchType.EAGER)
    private List<MembreBureau> membreBureauList;

    @ManyToMany(mappedBy = "clubList", fetch = FetchType.EAGER)
    private List<Adherent> adherentList;
}
