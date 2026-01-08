package ma.xproce.club_gestion.service;

import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Adherent;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.Evenement;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AdherentService implements  IAdherentService {

    @Autowired
    private AdherentRepository adherentRepository;

    @Override
    public List<Club> getClubsByAdherent(Long adherentId) {
        Adherent adherent = adherentRepository.findById(adherentId).orElse(null);
        if (adherent != null) {
            return adherent.getClubs();
        }
        return List.of();
    }

//    public Adherent getAdherentFromUser(Utilisateur user){
//        return adherentRepository.getById(user.getId());
//    }



//    public List<Evenement> getListOfAdherentEvents(Adherent adherent){
//        return adherent.getEvenements();
//    }
}
