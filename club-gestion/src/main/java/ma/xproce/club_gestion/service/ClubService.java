package ma.xproce.club_gestion.service;

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

        // 1. Vérification : Est-il déjà dans la liste des adhérents du club ?
        // On utilise les ID pour être certain de la comparaison
        boolean dejaAdherent = club.getAdherents().stream()
                .anyMatch(a -> a.getId().equals(utilisateurId));

        if (dejaAdherent) {
            throw new RuntimeException("Vous êtes déjà membre de ce club !");
        }

        // 2. Gestion du type : Si c'est un simple Utilisateur, il DOIT devenir Adherent
        Adherent adherent;
        if (user instanceof Adherent) {
            adherent = (Adherent) user;
        } else {
            // Logique de conversion (nécessaire à cause de l'héritage JPA)
            // Note: Supprimer l'ancien user est risqué mais parfois nécessaire
            // selon ta stratégie d'héritage si tu veux garder le même email.
            adherent = new Adherent();
            adherent.setId(user.getId()); // On essaie de garder le même ID
            adherent.setNom(user.getNom());
            adherent.setPrenom(user.getPrenom());
            adherent.setEmail(user.getEmail());
            adherent.setMotDePasse(user.getMotDePasse());
            adherent.setRole("ADHERENT");
            // On le sauvegarde pour qu'il devienne une entité Adherent gérée
            adherent = adherentRepository.save(adherent);
        }

        // 3. Mise à jour de la relation (CÔTÉ MAÎTRE)
        // C'est la liste 'adherents' dans Club qui possède le @JoinTable
        club.getAdherents().add(adherent);

        // 4. Mise à jour du côté inverse (pour la cohérence de l'objet en mémoire)
        adherent.getClubs().add(club);

        // 5. Persistance
        clubRepository.save(club);
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

}
