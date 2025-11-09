package ma.xproce.club_gestion;

import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
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
    public static void main(String[] args) {
        SpringApplication.run(ClubGestionApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        Utilisateur admin = new Utilisateur();
        admin.setNom("Admin");
        admin.setPrenom("Admin");
        admin.setEmail("admin@ecole.ma"); // <-- L'email en question
        admin.setMotDePasse("admin123");
        admin.setRole("ADMIN");
        utilisateurRepository.save(admin);
    }


//    @Bean
//    CommandLineRunner start(ClubRepository clubRepository) {
//        return args -> {
//            clubRepository.save(new Club(null, "Club Mechatronics", "Concevoir, construire et innover en intégrant mécanique, électronique et informatique pour créer des systèmes intelligents et automatisés."));
//            clubRepository.save(new Club(null, "Club ROTARACT", "Servir la communauté en développant le leadership et l'amitié par des actions caritatives."));
//            clubRepository.findAll().forEach(c ->
//                    System.out.println("Club : " + c.getNom()));
//        };
//    }

}
