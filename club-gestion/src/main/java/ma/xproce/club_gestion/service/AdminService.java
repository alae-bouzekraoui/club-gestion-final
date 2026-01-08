package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.DemandeClubRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ma.xproce.club_gestion.dao.entities.DemandeClub;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.entities.MembreBureau;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.List;

@Service
@Transactional
public class AdminService implements IAdminService {

    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private DemandeClubRepository demandeClubRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private MembreBureauRepository membreBureauRepository;

    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    @Override
    public void deleteClub(Long id) {
        clubRepository.deleteById(id);
    }

    @Override
    public List<DemandeClub> getDemandesEnAttente() {
        return demandeClubRepository.findByStatut("EN_ATTENTE");
    }

    @Override
    public void accepterDemande(Long demandeId) {
        DemandeClub demande = demandeClubRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        // 1. Création du club
        Club club = new Club();
        club.setNom(demande.getNom());
        club.setDescription(demande.getDescription());
        club.setObjectifs(demande.getObjectifs());
        clubRepository.save(club);

        // 2. Traitement des membres (Logique extraite de votre contrôleur)
        if (demande.getEmailsMembres() != null) {
            for (String email : demande.getEmailsMembres()) {
                if (email == null || email.isBlank()) continue;
                traiterUtilisateurPourClub(email.trim(), club);
            }
        }

        // 3. Suppression de la demande
        demandeClubRepository.delete(demande);
    }

    @Override
    public void refuserDemande(Long demandeId, String commentaire) {
        DemandeClub demande = demandeClubRepository.findById(demandeId).orElse(null);
        if (demande != null) {
            demande.setStatut("REFUSEE");
            demande.setCommentaireAdmin(commentaire);
            demandeClubRepository.save(demande);
        }
    }

    // Méthode privée utilitaire pour la clarté
    private void traiterUtilisateurPourClub(String email, Club club) {
        Utilisateur user = utilisateurRepository.findByEmail(email);
        if (user == null) return;

        if (!(user instanceof MembreBureau)) {
            user.setRole("MembreBureau");
            utilisateurRepository.save(user);
        }

        MembreBureau membre = membreBureauRepository.findById(user.getId()).orElse(null);
        if (membre != null) {
            if (membre.getClubList() == null) membre.setClubList(new ArrayList<>());
            membre.getClubList().add(club);
            membreBureauRepository.save(membre);
        }
    }
}