package ma.xproce.club_gestion.service;

import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
    private final UtilisateurRepository utilisateurRepository;

    public Utilisateur registerNewUser(Utilisateur user) {
        // Vérifier si l’email existe déjà
        if (utilisateurRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        user.setRole("USER");

        return utilisateurRepository.save(user);
    }
}
