package ma.xproce.club_gestion.dao.entities;

import jakarta.persistence.Entity;

import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Data
@PrimaryKeyJoinColumn(name = "id")
@ToString
public class Admin extends Utilisateur{

}
