package ma.xproce.club_gestion.dao.repositories;

import ma.xproce.club_gestion.dao.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
}
