package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.DemandeAdhesion;
import java.util.List;

public interface IDemandeAdhesionService {
    void creerDemandeAdhesion(String nom, String desc, String obj, Long clubId, Long userId);
    List<DemandeAdhesion> getDemandesParStatut(String statut);
}
