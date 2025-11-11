package ma.xproce.club_gestion.dao.entities;

import jakarta.persistence.Entity;

import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Data
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends Utilisateur{

}
