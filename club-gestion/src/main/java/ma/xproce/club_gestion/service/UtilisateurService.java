package ma.xproce.club_gestion.service;

import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    // Méthode d'inscription
    public Utilisateur registerNewUser(Utilisateur user) {
        Utilisateur existing = utilisateurRepository.findByEmail(user.getEmail());
        if (existing != null) {
            throw new RuntimeException("Email déjà utilisé !");
        }
        user.setRole("USER");
        return utilisateurRepository.save(user);
    }

    // Méthode de connexion
    public Utilisateur loginUser(String email, String password) {
        Utilisateur user = utilisateurRepository.findByEmail(email);
        if (user == null || !user.getMotDePasse().equals(password)) {
            throw new RuntimeException("Email ou mot de passe incorrect !");
        }
        return user;
    }
}
