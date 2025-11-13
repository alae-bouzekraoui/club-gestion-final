package ma.xproce.club_gestion;

import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.MembreBureau;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.DemandeClubRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClubGestionApplication implements CommandLineRunner {

    @Autowired
    public UtilisateurRepository utilisateurRepository;
    @Autowired
    public MembreBureauRepository membreBureauRepository;
    @Autowired
    private DemandeClubRepository demandeClubRepository;

    public static void main(String[] args) {
        SpringApplication.run(ClubGestionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        if (utilisateurRepository.findByEmail("admin@ecole.ma") == null) {
//            utilisateurRepository.save(new Utilisateur(null,"Admin", "Admin","admin@ensam.ma", "1234", "ADMIN"));

    }

    @Bean
    CommandLineRunner start(ClubRepository clubRepository) {
        return args -> {
            if (clubRepository.count() == 0) {
                clubRepository.save(new Club(null, "Club Informatique", "Découverte du développement et de l’IA", "Encourager l’innovation et la collaboration", "2022-09-10", null, null, null, null));
                clubRepository.save(new Club(null, "Club Sportif", "Promotion du sport à l’école", "Renforcer la cohésion et la santé", "2021-11-01", null, null, null, null));
                clubRepository.save(new Club(null, "Club Théâtre", "Passionnés de scène et d’expression orale", "Favoriser la créativité et la confiance", "2023-03-05", null, null, null, null));
                clubRepository.save(new Club(null, "Club Musique", "Groupe de musiciens amateurs", "Développer le talent artistique des étudiants", "2020-12-12", null, null, null, null));
            }
            demandeClubRepository.deleteAll();
        };
    }

}
