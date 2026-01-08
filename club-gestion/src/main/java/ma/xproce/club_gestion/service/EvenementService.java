package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.AdherentRepository;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.EvenementRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class EvenementService implements IEvenementService {

    @Autowired
    private EvenementRepository evenementRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private MembreBureauRepository membreBureauRepository;
    @Autowired
    private ClubRepository clubRepository;

    @Override
    @Transactional
    public List<Evenement> getEvenementsPourUtilisateur(Utilisateur user) {
        if (user == null) return new ArrayList<>();

        if (user instanceof Adherent) {
            Adherent adherent = adherentRepository.findById(user.getId()).orElse(null);
            if (adherent != null) {
                return recupererEvenementsAdherent(adherent);
            }
        }

        if (user instanceof MembreBureau) {
            MembreBureau mb = membreBureauRepository.findById(user.getId()).orElse(null);
            if (mb != null) {
                return recupererEvenementsDesClubsMembre(mb);
            }
        }

        return new ArrayList<>();
    }

    private List<Evenement> recupererEvenementsAdherent(Adherent a) {
        List<Evenement> events = new ArrayList<>();
        for (Club club : a.getClubs()) {
            events.addAll(club.getEvenementList());
        }
        return events;
    }

    private List<Evenement> recupererEvenementsDesClubsMembre(MembreBureau mb) {
        List<Evenement> events = new ArrayList<>();
        for (Club club : mb.getClubList()) {
            events.addAll(club.getEvenementList());
        }
        return events;
    }


    @Override
    @Transactional
    public void ajouterEvenement(Long clubId, Evenement evenement) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club non trouvé avec l'id : " + clubId));
        evenement.setClub(club);
        evenementRepository.save(evenement);
    }

    @Override
    @Transactional
    public void supprimerEvenement(Long id) {
        // On vérifie l'existence pour pouvoir lancer une exception personnalisée si besoin
        if (!evenementRepository.existsById(id)) {
            throw new RuntimeException("L'événement avec l'ID " + id + " n'existe pas.");
        }
        evenementRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean ajoutEvenementCalendrier(Long evenementId, Long utilisateurId) {
        Adherent adherent = adherentRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        if (adherent.getEvenements().contains(evenement)) {
            return false; // Déjà présent
        }

        adherent.getEvenements().add(evenement);
        adherentRepository.save(adherent);
        return true;
    }

    @Override
    @Transactional
    public void retirerEvenementDuCalendrier(Long evenementId, Long utilisateurId) {
        Adherent adherent = adherentRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));

        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        adherent.getEvenements().remove(evenement);
        adherentRepository.save(adherent);
    }

}
