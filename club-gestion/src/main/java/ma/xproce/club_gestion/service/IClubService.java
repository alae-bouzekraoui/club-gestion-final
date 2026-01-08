package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.DemandeClub;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import java.util.List;

public interface IClubService {
    List<Club> getAllClubs();
    Utilisateur rejoindreClub(Long clubId, Long utilisateurId);
    Utilisateur quitterClub(Long clubId, Long utilisateurId);
    Club getClubParNom(String nom);
    boolean estAdherentDuClub(Utilisateur user, Club club);
    void soumettreDemandeCreation(DemandeClub demande, List<String> emailsMembres, Long demandeurId);
}
