package ma.xproce.club_gestion.service;


import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Adherent;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.Evenement;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.AdherentRepository;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.EvenementRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final AdherentRepository adherentRepository;
    private final ClubRepository clubRepository;

    public Evenement ajouterEvenement(Club club, Evenement evenement) {
        evenement.setClub(club);
        return evenementRepository.save(evenement);
    }

    public void supprimerEvenement(Long id){
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        evenementRepository.delete(evenement);
    }

    public boolean ajoutEvenementCalendrier(Long eventId, Adherent adherent) {

        Evenement event = evenementRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));

        boolean existe = adherent.getEvenements().stream()
                .anyMatch(e -> e.getId().equals(event.getId()));

        if (existe) { return false; }

        event.getParticipants().add(adherent);
        evenementRepository.save(event);
        return true;
    }

    public boolean removeEventFromCalendar(Long eventId, Adherent adherent) {
        Evenement event = evenementRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));

        boolean exists = event.getParticipants().removeIf(p -> p.getId().equals(adherent.getId()));
        if (!exists) return false;

        evenementRepository.save(event);
        return true;
    }


}
