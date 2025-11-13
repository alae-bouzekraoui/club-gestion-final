package ma.xproce.club_gestion.dao.repositories;

import ma.xproce.club_gestion.dao.entities.DemandeClub;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DemandeClubRepository extends JpaRepository<DemandeClub, Long> {
    List<DemandeClub> findByStatut(String statut);
    List<DemandeClub> findByDemandeurId(Long demandeurId);
    void deleteAllByDemandeurId(Long demandeurId);
}