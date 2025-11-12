package ma.xproce.club_gestion.service;


import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.MembreBureau;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembreBureauSerive {

    private final MembreBureauRepository membreBureauRepository;

    public MembreBureau getMeembreFromUser (Utilisateur user){
        return membreBureauRepository.getById(user.getId());
    }


}
