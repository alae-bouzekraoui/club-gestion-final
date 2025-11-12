package ma.xproce.club_gestion.web;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.AdherentRepository;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import ma.xproce.club_gestion.service.AdherentService;
import ma.xproce.club_gestion.service.EvenementService;
import ma.xproce.club_gestion.service.MembreBureauSerive;
import ma.xproce.club_gestion.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UtilisateurService utilisateurService;
    private final MembreBureauSerive membreBureauSerive;
    private final ClubRepository clubRepository;
    private final AdherentRepository adherentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AdherentService adherentService;
    private final EvenementService evenementService;


    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Récupérer les clubs depuis la base de données
        List<Club> clubs = clubRepository.findAll();
        model.addAttribute("clubs", clubs);

        // Vérifier s'il y a un utilisateur connecté
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        model.addAttribute("user", user);

        return "index";
    }


    @GetMapping("/signin")
    public String login() {
        return "signin";
    }


    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }


    @PostMapping("/signin")
    public String loginUser(@ModelAttribute Utilisateur user, HttpSession session, Model model) {
        try {
            Utilisateur existingUser = utilisateurService.loginUser(user.getEmail(),user.getMotDePasse());
            session.setAttribute("user", existingUser);

            if ("ADMIN".equals(existingUser.getRole())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/";

        } catch (RuntimeException e) {
            // Si ça casse (email déjà pris), ajouter le message d'erreur au modèle
            model.addAttribute("error", e.getMessage());
            return "signin";
        }
    }


    @PostMapping("/signup")
    public String registerUser(@ModelAttribute Utilisateur user, HttpSession session, Model model) {
        try {
            // Enregistrer l’utilisateur dans la base
            utilisateurService.registerNewUser(user);
            session.setAttribute("user", user);
            return "redirect:/";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }


    @GetMapping("/mes-clubs")
    public String mesClubs(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        if (user instanceof Adherent adherent) {
            model.addAttribute("clubs", adherent.getClubs());
        } else {
            model.addAttribute("clubs", List.of());
        }

        model.addAttribute("user", user);
        return "mes-clubs";
    }


    @PostMapping("/clubs/{id}/adhesion")
    public String adhererClub(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        Club club = clubRepository.findById(id).get();

        //  Si déjà Adhérent
        if (user instanceof Adherent adherent) {
            if (adherent.getClubs().stream().anyMatch(c -> c.getId().equals(club.getId()))) {
                redirectAttributes.addFlashAttribute("message", "⚠ Vous êtes déjà membre de ce club !");
                redirectAttributes.addFlashAttribute("messageType", "warning");
                return "redirect:/";
            }

            adherent.getClubs().add(club);
            adherentRepository.save(adherent);
            club.getAdherents().add(adherent);
            clubRepository.save(club);

            session.setAttribute("user", adherent);
            redirectAttributes.addFlashAttribute("message", " Vous avez rejoint le club " + club.getNom());
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/";
        }

        //  Sinon : transformer l’utilisateur en Adhérent
        Adherent newAdherent = new Adherent();
        newAdherent.setNom(user.getNom());
        newAdherent.setPrenom(user.getPrenom());
        newAdherent.setEmail(user.getEmail());
        newAdherent.setMotDePasse(user.getMotDePasse());
        newAdherent.setRole("ADHERENT");


        utilisateurRepository.deleteById(user.getId());
        adherentRepository.save(newAdherent);

        newAdherent.getClubs().add(club);
        adherentRepository.save(newAdherent);
        club.getAdherents().add(newAdherent);
        clubRepository.save(club);

        // Mettre à jour la session
        session.setAttribute("user", newAdherent);

        redirectAttributes.addFlashAttribute("message", " Vous êtes désormais membre du club " + club.getNom() + " !");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/";
    }


    @PostMapping("/clubs/{id}/desadhesion")
    public String desadhererClub(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Object user = session.getAttribute("user");

        if (!(user instanceof Adherent adherent)) {
            redirectAttributes.addFlashAttribute("message", "⚠️ Vous devez être adhérent pour quitter un club !");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/signin";
        }

        Club club = clubRepository.findById(id).get();
        Adherent adherentInDb = adherentRepository.findById(adherent.getId()).get();

        club.getAdherents().remove(adherentInDb);
        clubRepository.save(club);
        adherentInDb.getClubs().remove(club);
        adherentRepository.save(adherentInDb);

        session.setAttribute("user", adherentInDb);

        redirectAttributes.addFlashAttribute("message", "🚫 Vous avez quitté le club " + club.getNom() + ".");
        redirectAttributes.addFlashAttribute("messageType", "info");

        return "redirect:/mes-clubs";
    }


    @GetMapping("/calendrier")
    public String calendrier(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        model.addAttribute("role", user.getRole());

        switch (user.getRole()){
            case "AHERENT":
                Adherent adherent = adherentService.getAdherentFromUser(user);
                List<Evenement> adherentEvenements = adherentService.getListOfAdherentEvents(adherent);

                if (adherentEvenements == null) {
                    adherentEvenements = new ArrayList<>();
                }

                model.addAttribute("adherentEvenements", adherentEvenements);
                break;

            case "MembreBureau":
                MembreBureau membreBureau = membreBureauSerive.
                List<Evenement> adherentEvenements = adherentService.getListOfAdherentEvents(adherent);

                if (adherentEvenements == null) {
                    adherentEvenements = new ArrayList<>();
                }

                model.addAttribute("adherentEvenements", adherentEvenements);
                break;

        }

        model.addAttribute("user", user);
        return "calendrier";
    }


    @GetMapping("/clubs/{nom}")
    public String voirClub(@PathVariable String nom, Model model) {
        Club club = clubRepository.findByNom(nom);
        model.addAttribute("club", club);
        return "club-details";
    }

    @GetMapping("/evenements")
    public String showEvenements(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "Veuillez vous connecter pour accéder aux événements.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/signin";
        }

        model.addAttribute("role", user.getRole());

        switch (user.getRole()) {
            case "ADHERENT":
                Adherent adherent = adherentService.getAdherentFromUser(user) ;
                List<Club> clubsAdheres = adherent.getClubs();

                if (clubsAdheres == null) {
                    clubsAdheres = new ArrayList<>();
                }
                model.addAttribute("clubsAdheres", clubsAdheres);
                break;

            case "MembreBureau":
                MembreBureau membre = membreBureauSerive.getMeembreFromUser(user) ;
                List<Club> clubsMembreBureau = membre.getClubList();
                if (clubsMembreBureau == null) {
                    clubsMembreBureau = new ArrayList<>();
                }
                model.addAttribute("clubsMembreBureau", clubsMembreBureau);
                break;

            default:

                break;
        }
        model.addAttribute("user", user);
        return "evenements";
    }

    @PostMapping("/evenements/add")
    public String addEvenement(
            @ModelAttribute Evenement evenement,

            @RequestParam("clubId") Long clubId,

            RedirectAttributes redirectAttributes
    ) {

        Club club = clubRepository.findById(clubId)
                .orElse(null);

        if (club == null) {
            redirectAttributes.addFlashAttribute("message", "Erreur : Club non trouvé.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/evenements";
        }

        try {
            evenementService.ajouterEvenement(club, evenement);
            redirectAttributes.addFlashAttribute("message", "Événement ajouté avec succès !");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Une erreur est survenue lors de l'ajout.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/evenements";
    }

    @PostMapping("/evenements/supprimer/{id}")
    public String supprimerEvenement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            evenementService.supprimerEvenement(id);
            redirectAttributes.addFlashAttribute("message", "Événement supprimé avec succès !");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur lors de la suppression de l’événement.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/evenements";
    }

    @PostMapping("/evenements/Ajout_au_calendrier/{id}")
    public String ajoutEvenemetCalendrier(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ){
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "Veuillez vous connecter pour accéder aux événements.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/signin";
        }
        Adherent adherent = adherentService.getAdherentFromUser(user);

        evenementService.ajoutEvenementCalendrier(id,adherent);

        return "calendrier";

    }

}
