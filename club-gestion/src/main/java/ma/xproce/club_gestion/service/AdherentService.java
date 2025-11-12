package ma.xproce.club_gestion.service;

import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Adherent;
import ma.xproce.club_gestion.dao.entities.MembreBureau;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.AdherentRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdherentService {
    private final AdherentRepository adherentRepository;

    public Adherent getAdherentFromUser(Utilisateur user){
        return adherentRepository.getById(user.getId());
    }
}
