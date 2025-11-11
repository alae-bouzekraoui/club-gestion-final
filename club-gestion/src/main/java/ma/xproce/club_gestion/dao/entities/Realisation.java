package ma.xproce.club_gestion.dao.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Realisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    private Date date;

    //relation club-realisation
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    //relation membreBureau-realisation
    @ManyToOne
    @JoinColumn(name = "membre_bureau_id")
    private MembreBureau membreBureau;

}
