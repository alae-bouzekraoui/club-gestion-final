package ma.xproce.club_gestion.service;

import jakarta.persistence.EntityManager;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.DemandeClubRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    @Autowired
    private EntityManager entityManager;

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
    public void accepterDemande(Long demandeId, String poste) {
        DemandeClub demande = demandeClubRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        // 1. Création du club
        Club club = new Club();
        club.setNom(demande.getNom());
        club.setDescription(demande.getDescription());
        club.setObjectifs(demande.getObjectifs());
        clubRepository.save(club);

        // 2. Si le demandeur existe, le promouvoir en MembreBureau et lui attribuer le poste
        Utilisateur demandeur = demande.getDemandeur();
        if (demandeur != null) {
            // promotion et ajout au club
            traiterUtilisateurPourClub(demandeur.getEmail(), club);

            // recharger en tant que MembreBureau si possible
            MembreBureau membre = membreBureauRepository.findById(demandeur.getId()).orElse(null);
            if (membre == null) {
                Utilisateur u = utilisateurRepository.findById(demandeur.getId()).orElse(null);
                if (u instanceof MembreBureau) membre = (MembreBureau) u;
            }

            if (membre != null) {
                String appliedPoste = (poste == null || poste.isBlank()) ? "PRESIDENT" : poste;
                membre.setPoste(appliedPoste);
                membreBureauRepository.save(membre);
            }
        }

        // 3. Traitement des autres membres listés dans la demande
        if (demande.getEmailsMembres() != null) {
            for (String email : demande.getEmailsMembres()) {
                if (email == null || email.isBlank()) continue;
                // éviter de double-traiter le demandeur
                if (demandeur != null && email.trim().equalsIgnoreCase(demandeur.getEmail())) continue;
                traiterUtilisateurPourClub(email.trim(), club);
            }
        }

        // 4. Suppression de la demande
        demandeClubRepository.delete(demande);
    }

    // Méthode privée utilitaire pour la clarté
    private void traiterUtilisateurPourClub(String email, Club club) {
        Utilisateur user = utilisateurRepository.findByEmail(email.trim());
        if (user == null) return;

        MembreBureau membreBureau;

        // 1. Vérifier si l'utilisateur est déjà un MembreBureau
        if (user instanceof MembreBureau) {
            membreBureau = (MembreBureau) user;
        }
        else {
            // 2. Si l'utilisateur est un Adhérent, il faut d'abord supprimer ses associations avec les clubs
            // car un utilisateur ne peut pas être à la fois dans la table 'adherent' et 'membre_bureau'
            if (user instanceof Adherent) {
                entityManager.createNativeQuery("DELETE FROM adherent_club WHERE adherent_id = ?")
                        .setParameter(1, user.getId())
                        .executeUpdate();
                
                entityManager.createNativeQuery("DELETE FROM adherent WHERE id = ?")
                        .setParameter(1, user.getId())
                        .executeUpdate();
            }

            // 3. Vérifier si l'utilisateur existe déjà dans membre_bureau (au cas où il aurait déjà été promu)
            Number count = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM membre_bureau WHERE id = ?")
                    .setParameter(1, user.getId())
                    .getSingleResult();
            
            if (count.longValue() == 0) {
                // 3a. Promotion SQL en MembreBureau uniquement s'il n'existe pas déjà
                entityManager.createNativeQuery("INSERT INTO membre_bureau (id) VALUES (?)")
                        .setParameter(1, user.getId())
                        .executeUpdate();
            }

            user.setRole("MEMBREBUREAU");
            utilisateurRepository.save(user);

            // 4. RESET obligatoire du contexte Hibernate
            entityManager.flush();
            entityManager.clear();

            // 5. Recharger l'objet en tant que MembreBureau
            membreBureau = membreBureauRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Erreur de rechargement après promotion"));
        }

        // 6. Ajouter le membre au club
        if (club.getMembreBureauList() == null) club.setMembreBureauList(new ArrayList<>());

        if (!club.getMembreBureauList().contains(membreBureau)) {
            club.getMembreBureauList().add(membreBureau);
            clubRepository.save(club);
        }
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


}