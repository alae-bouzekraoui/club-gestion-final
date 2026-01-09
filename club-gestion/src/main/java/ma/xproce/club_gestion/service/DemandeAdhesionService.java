package ma.xproce.club_gestion.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemandeAdhesionService implements IDemandeAdhesionService {
    @Autowired
    private DemandeAdhesionRepository demandeAdhesionRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private MembreBureauRepository membreBureauRepository;

    @Transactional
    @Override
    public void creerDemandeAdhesion(String nom, String desc, String obj, Long clubId, Long userId) {

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Club introuvable"));
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        // 1. Vérifier si l'utilisateur est déjà adhérent
        if (user instanceof Adherent adherent) {
            if (club.getAdherents().contains(adherent)) {
                throw new IllegalStateException("Vous êtes déjà adhérent de ce club.");
            }
        }

        // 2. Vérifier s'il existe déjà une demande en attente
        List<DemandeAdhesion> demandesExistantes = demandeAdhesionRepository
                .findByClubIdAndDemandeurIdAndStatut(clubId, userId, "EN_ATTENTE");

        if (!demandesExistantes.isEmpty()) {
            throw new IllegalStateException("Vous avez déjà une demande en attente pour ce club.");
        }

        // 3. Création de la demande
        DemandeAdhesion demande = new DemandeAdhesion();
        demande.setStatut("EN_ATTENTE");
        demande.setDateDemande(LocalDateTime.now());
        demande.setClub(club);
        demande.setDemandeur(user);

        demandeAdhesionRepository.save(demande);
    }

    @Override
    public List<DemandeAdhesion> getDemandesParStatut(String statut) {
        return demandeAdhesionRepository.findByStatut(statut);
    }

    @Transactional(readOnly = true)
    public boolean hasPendingRequest(Long utilisateurId, Long clubId) {
        List<DemandeAdhesion> demandesExistantes = demandeAdhesionRepository
                .findByClubIdAndDemandeurIdAndStatut(clubId, utilisateurId, "EN_ATTENTE");
        return !demandesExistantes.isEmpty();
    }

    @Transactional(readOnly = true)
    public List<DemandeAdhesion> getPendingRequestsForClubs(List<Club> clubs) {
        if (clubs == null || clubs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> clubIds = clubs.stream()
                .map(Club::getId)
                .collect(Collectors.toList());

        return demandeAdhesionRepository.findByClubIdInAndStatut(clubIds, "EN_ATTENTE");
    }

    @Transactional
    public void accepterDemandeAdhesion(Long demandeAdhesionId) {
        DemandeAdhesion demandeAdhesion = demandeAdhesionRepository.findById(demandeAdhesionId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        Utilisateur user = demandeAdhesion.getDemandeur();
        Club club = demandeAdhesion.getClub();

        // Sécurisation du lien avant transformation
        demandeAdhesion.setDemandeur(null);
        demandeAdhesionRepository.saveAndFlush(demandeAdhesion);

        switch (user.getRole()) {
            case "USER":
                // Transformation d'un simple Utilisateur en Adherent
                Adherent adherent = new Adherent();
                adherent.setNom(user.getNom());
                adherent.setPrenom(user.getPrenom());
                adherent.setEmail(user.getEmail());
                adherent.setMotDePasse(user.getMotDePasse());
                adherent.setRole("ADHERENT");
                adherent.setClubs(new ArrayList<>());

                utilisateurRepository.delete(user);
                utilisateurRepository.flush(); // Indispensable pour libérer l'email

                Adherent newAdherent = adherentRepository.save(adherent);
                newAdherent.getClubs().add(club);
                club.getAdherents().add(newAdherent);

                demandeAdhesion.setDemandeur(newAdherent);
                break;

            case "ADHERENT":
                Adherent adh = (Adherent) user;
                adh.getClubs().add(club);
                club.getAdherents().add(adh);
                demandeAdhesion.setDemandeur(adh);
                break;

            case "MembreBureau":
                MembreBureau mb = (MembreBureau) user;

                // 1. Utilisez le nom exact de votre variable : clubList ou clubListAdherent
                if (mb.getClubList() != null) {
                    mb.getClubList().add(club);
                }

                // 2. Attention : club.getAdherents().add(mb) sera toujours rouge
                // car MembreBureau n'est pas un Adherent dans votre code (il hérite d'Utilisateur)
                // Vous devez l'ajouter à la liste spécifique des membres de bureau du club
                if (club.getMembreBureauList() != null) {
                    club.getMembreBureauList().add(mb);
                }

                demandeAdhesion.setDemandeur(mb);
                break;
        }

        demandeAdhesion.setStatut("ACCEPTED");
        demandeAdhesionRepository.save(demandeAdhesion);
    }

    public DemandeAdhesion getById(Long demandeAdhesionId) {
        return demandeAdhesionRepository.findById(demandeAdhesionId).orElse(null);
    }

    @Transactional
    public void refuserDemandeAdhesion(Long demandeAdhesionId) {
        DemandeAdhesion demandeAdhesion = demandeAdhesionRepository.findById(demandeAdhesionId)
                .orElseThrow(() -> new EntityNotFoundException("Demande introuvable"));
        demandeAdhesion.setStatut("REFUSE");
        demandeAdhesionRepository.save(demandeAdhesion);
    }
}