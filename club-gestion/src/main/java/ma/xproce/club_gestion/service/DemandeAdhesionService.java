package ma.xproce.club_gestion.service;

import jakarta.persistence.EntityNotFoundException;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.DemandeAdhesion;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.DemandeAdhesionRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DemandeAdhesionService implements IDemandeAdhesionService {

    @Autowired
    private DemandeAdhesionRepository demandeAdhesionRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Transactional
    @Override
    public void creerDemandeAdhesion(String nom, String desc, String obj, Long clubId, Long userId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Club introuvable"));
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User introuvable"));

        DemandeAdhesion demande = new DemandeAdhesion();
        demande.setNom(nom);
        demande.setDescription(desc);
        demande.setObjectifs(obj);
        demande.setStatut("EN_ATTENTE");
        demande.setClub(club);
        demande.setDemandeur(user);

        demandeAdhesionRepository.save(demande);
    }

    @Override
    public List<DemandeAdhesion> getDemandesParStatut(String statut) {
        return demandeAdhesionRepository.findByStatut(statut);
    }
}