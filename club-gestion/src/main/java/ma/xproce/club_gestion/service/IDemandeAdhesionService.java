package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.DemandeAdhesion;
import ma.xproce.club_gestion.dao.entities.Club;
import java.util.List;

public interface IDemandeAdhesionService {

    void creerDemandeAdhesion(String nom, String desc, String obj, Long clubId, Long userId);
    List<DemandeAdhesion> getDemandesParStatut(String statut);

    boolean hasPendingRequest(Long utilisateurId, Long clubId);
    List<DemandeAdhesion> getPendingRequestsForClubs(List<Club> clubs);

    void accepterDemandeAdhesion(Long demandeAdhesionId);
    void refuserDemandeAdhesion(Long demandeAdhesionId);

    DemandeAdhesion getById(Long demandeAdhesionId);
}