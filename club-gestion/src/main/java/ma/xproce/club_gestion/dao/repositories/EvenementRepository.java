package ma.xproce.club_gestion.dao.repositories;

import ma.xproce.club_gestion.dao.entities.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
}
