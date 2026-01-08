package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.Utilisateur;

public interface IUtilisateurService {
    Utilisateur registerNewUser(Utilisateur user);
    Utilisateur loginUser(String email, String password);
    Utilisateur getById(Long id);
}