package ma.xproce.club_gestion.service;


import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Adherent;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.Evenement;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.EvenementRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;
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
    public void ajoutEvenementCalendrier(Long id, Adherent adherent){
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        evenement.getParticipants().add(adherent);

        evenementRepository.save(evenement);
    }


}
