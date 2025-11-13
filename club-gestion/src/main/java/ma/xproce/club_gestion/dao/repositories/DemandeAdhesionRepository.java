package ma.xproce.club_gestion.dao.repositories;

import ma.xproce.club_gestion.dao.entities.DemandeAdhesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeAdhesionRepository extends JpaRepository<DemandeAdhesion,Long> {

    List<DemandeAdhesion> findByClubIdAndDemandeurIdAndStatut(Long clubId, Long utilisateurId, String enAttente);

    List<DemandeAdhesion> findByStatut(String statut);
}
