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
public class MembreBureauSerive {

    private final MembreBureauRepository membreBureauRepository;

    public MembreBureau getMeembreFromUser (Utilisateur user){
        return membreBureauRepository.getById(user.getId());
    }

    public List<Evenement> getListOfMembreBureauEvents(MembreBureau membreBureau){
        return membreBureau.getEvenementOrganises();
    }


}
