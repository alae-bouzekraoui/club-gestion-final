package ma.xproce.club_gestion;

import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.DemandeClubRepository;
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
    private DemandeClubRepository demandeClubRepository;

    public static void main(String[] args) {
        SpringApplication.run(ClubGestionApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (utilisateurRepository.findByEmail("admin@ecole.ma") == null) {
            utilisateurRepository.save(new Utilisateur(null, "Admin", "Admin", "admin@ecole.ma", "1234", "ADMIN"));
        }
    }

    @Bean
    CommandLineRunner start(ClubRepository clubRepository) {
        return args -> {
            if (clubRepository.count() == 0) {
                Club informatique = new Club();
                informatique.setNom("Club Informatique");
                informatique.setDescription("Découverte du développement et de l’IA");
                informatique.setObjectifs("Encourager l’innovation et la collaboration");
                informatique.setDateCreation("2022-09-10");
                clubRepository.save(informatique);

                Club sportif = new Club();
                sportif.setNom("Club Sportif");
                sportif.setDescription("Promotion du sport à l’école");
                sportif.setObjectifs("Renforcer la cohésion et la santé");
                sportif.setDateCreation("2021-11-01");
                clubRepository.save(sportif);

                Club theatre = new Club();
                theatre.setNom("Club Théâtre");
                theatre.setDescription("Passionnés de scène et d’expression orale");
                theatre.setObjectifs("Favoriser la créativité et la confiance");
                theatre.setDateCreation("2023-03-05");
                clubRepository.save(theatre);

                Club musique = new Club();
                musique.setNom("Club Musique");
                musique.setDescription("Groupe de musiciens amateurs");
                musique.setObjectifs("Développer le talent artistique des étudiants");
                musique.setDateCreation("2020-12-12");
                clubRepository.save(musique);
            }
            demandeClubRepository.deleteAll();
        };
    }

}
