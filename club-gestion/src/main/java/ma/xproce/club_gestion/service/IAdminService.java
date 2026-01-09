package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.DemandeClub;

import java.util.List;

public interface IAdminService {
    // Gestion des Clubs
    List<Club> getAllClubs();
    void deleteClub(Long id);

    // Gestion des Demandes
    List<DemandeClub> getDemandesEnAttente();
    void accepterDemande(Long demandeId, String poste);
    void refuserDemande(Long demandeId, String commentaire);
}
