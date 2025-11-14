package ma.xproce.club_gestion.service;

import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.AdherentRepository;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdherentService {
    private final AdherentRepository adherentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClubRepository clubRepository;

    public Adherent getAdherentFromUser(Utilisateur user){
        return adherentRepository.getById(user.getId());
    }


    public List<Evenement> getListOfAdherentEvents(Adherent adherent){
        return adherent.getEvenements();
    }

    public boolean isUserMember(Long userId, Long clubId) {

        // 1. Essayer de récupérer l'adhérent directement.
        // Si l'utilisateur n'est même pas un "Adherent" (rôle),
        // il ne peut être membre d'aucun club.
        Adherent adherent = adherentRepository.findById(userId).orElse(null);

        // 2. Si l'adhérent n'existe pas, il n'est pas membre.
        if (adherent == null) {
            return false;
        }

        Club club = clubRepository.findById(clubId).orElse(null);


        if (club == null) {
            return false;
        }

        return club.getAdherents().contains(adherent);
    }
}
