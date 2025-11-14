package ma.xproce.club_gestion.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeAdhesionService {

    private final DemandeAdhesionRepository demandeAdhesionRepository;
    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AdherentRepository adherentRepository;
    private final MembreBureauRepository membreBureauRepository;

    @Transactional
    public void ajouterDemandeAdhesion(
            Long clubId,
            Utilisateur user) {

        Long utilisateurId = user.getId();

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
        demandeAdhesion.setClub(club);
        demandeAdhesion.setDemandeur(demandeur);

        demandeAdhesion.setStatut("EN_ATTENTE");
        demandeAdhesion.setDateDemande(LocalDateTime.now());

        demandeAdhesionRepository.save(demandeAdhesion);
    }

    public List<DemandeAdhesion> findByStatut(String statut){
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

        // Si la liste est vide, inutile d'aller plus loin
        if (clubs == null || clubs.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Transformer la List<Club> en List<Long> (liste d'IDs)
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

        // 1. TRES IMPORTANT : Casser le lien avec l'ancien utilisateur
        // avant de le supprimer, pour éviter les problèmes de contrainte
        demandeAdhesion.setDemandeur(null);
        demandeAdhesionRepository.save(demandeAdhesion);

        switch (user.getRole()) {

            case "USER":
                // 2. Créer l'objet transient
                Adherent adherent = new Adherent();
                adherent.setNom(user.getNom());
                adherent.setPrenom(user.getPrenom());
                adherent.setEmail(user.getEmail());
                adherent.setMotDePasse(user.getMotDePasse());
                adherent.setRole("ADHERENT");
                adherent.setClubs(new ArrayList<>());

                // 3. Supprimer l'ancien utilisateur
                utilisateurRepository.delete(user);

                // 4. Forcer l'exécution du DELETE pour libérer l'email (corrige le DuplicateKey)
                utilisateurRepository.flush();

                // 5. SAUVEGARDER le nouvel adhérent. Il est maintenant PERSISTANT.
                Adherent newAdherent = adherentRepository.save(adherent);

                // 6. MAINTENANT, créer les relations en utilisant l'objet persistent
                newAdherent.getClubs().add(club);
                if (club.getAdherents() == null) {
                    club.setAdherents(new ArrayList<>());
                }
                club.getAdherents().add(newAdherent); // Ajouter l'objet newAdherent (persistant)

                // 7. Mettre à jour la demande avec le nouvel adhérent
                demandeAdhesion.setDemandeur(newAdherent);
                break;

            case "ADHERENT":
                Adherent adh = adherentRepository.findById(user.getId())
                        .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

                adh.getClubs().add(club);
                if (club.getAdherents() == null) {
                    club.setAdherents(new ArrayList<>());
                }
                club.getAdherents().add(adh);

                // Pas besoin de .save() ici, la transaction s'en occupe,
                // mais on le garde pour récupérer l'objet à jour
                Adherent updatedAdh = adherentRepository.save(adh);
                demandeAdhesion.setDemandeur(updatedAdh);
                break;

            case "MembreBureau":
                MembreBureau mb = membreBureauRepository.findById(user.getId())
                        .orElseThrow(() -> new RuntimeException("Membre bureau non trouvé"));

                mb.getClubListAdherent().add(club);
                if (club.getAdherents() == null) {
                    club.setAdherents(new ArrayList<>());
                }
                // Un MembreBureau EST un Adherent, il va dans la liste des adhérents
                club.getMembreBureauAdheranList().add(mb);

                MembreBureau updatedMb = membreBureauRepository.save(mb);
                demandeAdhesion.setDemandeur(updatedMb);
                break;
        }

        // 8. Sauvegarde finale de la demande (statut + lien demandeur)
        demandeAdhesion.setStatut("ACCEPTED");
        demandeAdhesionRepository.save(demandeAdhesion);
    }


    public DemandeAdhesion getById(Long demandeAdhesionId){
        return demandeAdhesionRepository.findById(demandeAdhesionId).orElse(null);
    }

    public void refuserDemandeAdhesion(Long demandeAdhesionId){
        DemandeAdhesion demandeAdhesion = demandeAdhesionRepository.findById(demandeAdhesionId).orElse(null);
        demandeAdhesion.setStatut("REFUSE");
        demandeAdhesionRepository.save(demandeAdhesion);
    }
}