package ma.xproce.club_gestion.service;

import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;


    public Club getClubById(Long clubId){
        return clubRepository.findById(clubId)
                .orElse(null);

    }

    public Club getClubByNom(String clubNom) {
        return clubRepository.findByNom(clubNom);
    }
}
