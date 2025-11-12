package ma.xproce.club_gestion.service;


import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.Evenement;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.EvenementRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final ClubRepository clubRepository;

    public Evenement ajouterEvenement(Club club, Evenement evenement){
        evenement.setClub(club);
        club.getEvenementList().add(evenement);

        clubRepository.save(club);

        return   evenementRepository.save(evenement);
    }

}
