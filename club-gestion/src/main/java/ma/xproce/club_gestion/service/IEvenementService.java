package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.Evenement;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import java.util.List;

public interface IEvenementService {
    List<Evenement> getEvenementsPourUtilisateur(Utilisateur user);
    void ajouterEvenement(Long clubId, Evenement evenement);
    void supprimerEvenement(Long id);
    boolean ajoutEvenementCalendrier(Long evenementId, Long utilisateurId);
    void retirerEvenementDuCalendrier(Long evenementId, Long utilisateurId);

}
