package ma.xproce.club_gestion.dao.repositories;

import ma.xproce.club_gestion.dao.entities.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdherentRepository extends JpaRepository<Adherent, Long> {
    Adherent findByEmail(String email);

}
