package ma.xproce.club_gestion.service;

import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Adherent;
import ma.xproce.club_gestion.dao.entities.Evenement;
import ma.xproce.club_gestion.dao.entities.MembreBureau;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.AdherentRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdherentService {
    private final AdherentRepository adherentRepository;

    public Adherent getAdherentFromUser(Utilisateur user){
        return adherentRepository.getById(user.getId());
    }


    public List<Evenement> getListOfAdherentEvents(Adherent adherent){
        return adherent.getEvenements();
    }
}
