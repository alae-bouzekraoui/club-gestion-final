package ma.xproce.club_gestion.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Adherent;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.DemandeAdhesion;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.AdherentRepository;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.DemandeAdhesionRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeAdhesionService {

    private final DemandeAdhesionRepository demandeAdhesionRepository;
    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AdherentRepository adherentRepository; // Ajout nécessaire pour la validation

    @Transactional
    public void creerDemandeAdhesion(
            String nom,
            String description,
            String objectifs,
            Long clubId,
            Long utilisateurId) {


        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Club non trouvé avec ID: " + clubId));

        Utilisateur demandeur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec ID: " + utilisateurId));


        Adherent adherent = adherentRepository.findById(utilisateurId).orElse(null);
        if (adherent != null && club.getAdherents().contains(adherent)) {
            throw new IllegalStateException("Vous êtes déjà adhérent de ce club.");
        }

        List<DemandeAdhesion> demandesExistantes = demandeAdhesionRepository
                .findByClubIdAndDemandeurIdAndStatut(clubId, utilisateurId, "EN_ATTENTE");

        if (!demandesExistantes.isEmpty()) {
            throw new IllegalStateException("Vous avez déjà une demande en attente pour ce club.");
        }

        DemandeAdhesion demandeAdhesion = new DemandeAdhesion();
        demandeAdhesion.setNom(nom);
        demandeAdhesion.setDescription(description);
        demandeAdhesion.setObjectifs(objectifs);
        demandeAdhesion.setClub(club);
        demandeAdhesion.setDemandeur(demandeur);

        demandeAdhesion.setStatut("EN_ATTENTE");
        demandeAdhesion.setDateDemande(LocalDateTime.now());

        demandeAdhesionRepository.save(demandeAdhesion);
    }

    public List<DemandeAdhesion> findByStatut(String statut){
        return demandeAdhesionRepository.findByStatut(statut);
    }
}