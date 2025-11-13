package ma.xproce.club_gestion.service;


import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Adherent;
import ma.xproce.club_gestion.dao.entities.Evenement;
import ma.xproce.club_gestion.dao.entities.MembreBureau;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembreBureauService {

    private final MembreBureauRepository membreBureauRepository;

    public MembreBureau getMembreFromUser(Utilisateur user) {
        return membreBureauRepository.findById(user.getId())
                .orElse(null);
    }

    public List<Evenement> getListOfEventsForMembre(MembreBureau membre) {
        return membre.getEvenementOrganises();
    }
}
