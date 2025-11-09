package ma.xproce.club_gestion;

import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClubGestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubGestionApplication.class, args);
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
