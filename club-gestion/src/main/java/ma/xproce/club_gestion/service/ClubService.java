package ma.xproce.club_gestion.service;

import jakarta.persistence.EntityManager;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClubService implements  IClubService{

    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private MembreBureauRepository membreBureauRepository;
    @Autowired
    private DemandeClubRepository demandeClubRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    @Override
    @Transactional
    public Utilisateur rejoindreClub(Long clubId, Long utilisateurId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Adherent adherent;

        if (user instanceof Adherent) {
            adherent = (Adherent) user;
        } else {
            // AU LIEU DE SUPPRIMER, ON FAIT UNE PROMOTION SQL
            // On insère l'ID dans la table fille (adherent)
            entityManager.createNativeQuery("INSERT INTO adherent (id) VALUES (?)")
                    .setParameter(1, utilisateurId)
                    .executeUpdate();

            // On met à jour le rôle dans la table parente
            user.setRole("ADHERENT");
            utilisateurRepository.save(user);

            // Crucial : On force Hibernate à oublier l'ancien objet 'Utilisateur'
            // et à recharger le nouvel objet 'Adherent' depuis la base
            entityManager.flush();
            entityManager.clear();

            adherent = adherentRepository.findById(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Erreur lors de la promotion en Adhérent"));
        }

        // Maintenant on peut faire l'adhésion normalement
        if (!club.getAdherents().contains(adherent)) {
            club.getAdherents().add(adherent);
            adherent.getClubs().add(club);
            clubRepository.save(club);
        }

        return adherent;
    }

    @Override
    @Transactional
    public Utilisateur quitterClub(Long clubId, Long utilisateurId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        club.getAdherents().removeIf(adherent -> adherent.getId().equals(utilisateurId));

        if (user instanceof Adherent adherent) {
            adherent.getClubs().remove(club);
        }

        clubRepository.save(club);
        return utilisateurRepository.save(user);
    }

    @Override
    public Club getClubParNom(String nom) {
        return clubRepository.findByNom(nom);
    }

    @Override
    public boolean estAdherentDuClub(Utilisateur user, Club club) {
        if (user instanceof Adherent && club != null) {
            Adherent adherent = (Adherent) user;
            return club.getAdherents().stream()
                    .anyMatch(a -> a.getId().equals(adherent.getId()));
        }
        return false;
    }

    @Override
    @Transactional
    public void soumettreDemandeCreation(DemandeClub demande, List<String> emailsMembres, Long demandeurId) {
        for (String email : emailsMembres) {
            if (!utilisateurRepository.existsByEmail(email)) {
                throw new RuntimeException("L'email " + email + " n'existe pas dans le système !");
            }
        }

        Utilisateur demandeur = utilisateurRepository.findById(demandeurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        demande.setDemandeur(demandeur);
        demande.setStatut("EN_ATTENTE");
        demande.setEmailsMembres(emailsMembres);

        demandeClubRepository.save(demande);
    }

    public List<Club> getClubsByAdherent(Long adherentId) {
        Adherent adherent = adherentRepository.findById(adherentId)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
        return adherent.getClubs();
    }

    public List<Club> getClubsByMembreBureau(Long membreBureauId) {
        MembreBureau membreBureau = membreBureauRepository.findById(membreBureauId)
                .orElseThrow(() -> new RuntimeException("Membre du bureau non trouvé"));
        return membreBureau.getClubList();
    }

    public Club getClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club non trouvé avec l'ID : " + clubId));
    }

}
