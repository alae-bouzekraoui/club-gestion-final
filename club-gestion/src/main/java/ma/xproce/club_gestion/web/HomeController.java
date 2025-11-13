package ma.xproce.club_gestion.web;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.*;
import ma.xproce.club_gestion.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final MembreBureauRepository membreBureauRepository;
    private final DemandeClubRepository demandeClubRepository;
    private final DemandeAdhesionService demandeAdhesionService;


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

        if (user instanceof Adherent adherent) {
            if (adherent.getClubs().stream().anyMatch(c -> c.getId().equals(club.getId()))) {
                redirectAttributes.addFlashAttribute("message", "Vous êtes déjà membre de ce club !");
                redirectAttributes.addFlashAttribute("messageType", "warning");
                return "redirect:/";
            }

            adherent.getClubs().add(club);
            adherentRepository.save(adherent);
            club.getAdherents().add(adherent);
            clubRepository.save(club);

            session.setAttribute("user", adherent);
            redirectAttributes.addFlashAttribute("message", "Vous avez rejoint le club " + club.getNom());
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/";
        }

        if (user instanceof MembreBureau membreBureau) {
            if (membreBureau.getClubList().stream().anyMatch(c -> c.getId().equals(club.getId()))) {
                redirectAttributes.addFlashAttribute("message", "Vous êtes déjà membre de ce club !");
                redirectAttributes.addFlashAttribute("messageType", "warning");
                return "redirect:/";
            }

            membreBureau.getClubList().add(club);
            membreBureauRepository.save(membreBureau);
            club.getMembreBureauList().add(membreBureau);
            clubRepository.save(club);

            session.setAttribute("user", membreBureau);
            redirectAttributes.addFlashAttribute("message", "✅ Vous êtes désormais lié au club " + club.getNom() + " en tant que membre du bureau !");
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

    @PostMapping("/clubs/{clubId}/adhesion")
    public String creerDemandeAdhesion(@PathVariable("clubId") Long clubId,
                                       // Ces champs viennent du formulaire
                                       @RequestParam("nom") String nom,
                                       @RequestParam("description") String description,
                                       @RequestParam("objectifs") String objectifs,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {

        Object userObj = session.getAttribute("user");
        if (!(userObj instanceof Utilisateur userInSession)) {
            redirectAttributes.addFlashAttribute("message", "Vous devez être connecté pour faire une demande.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/signin";
        }

        try {
            demandeAdhesionService.creerDemandeAdhesion(
                    nom,
                    description,
                    objectifs,
                    clubId,
                    userInSession.getId()
            );

            redirectAttributes.addFlashAttribute("message", "Votre demande d'adhésion a été envoyée !");
            redirectAttributes.addFlashAttribute("messageType", "success");

        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "warning");
        }

        return "redirect:/clubs/" + clubId;
    }


    @GetMapping("/calendrier")
    public String calendrier(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        model.addAttribute("role", user.getRole());
        List<Evenement> evenements = new ArrayList<>();

//        switch (user.getRole()){
//            case "AHERENT":
//                Adherent adherent = adherentService.getAdherentFromUser(user);
//                List<Evenement> adherentEvenements = adherentService.getListOfAdherentEvents(adherent);
//
//                if (adherentEvenements == null) {
//                    adherentEvenements = new ArrayList<>();
//                }
//
//                model.addAttribute("adherentEvenements", adherentEvenements);
//                break;
//
//            case "MembreBureau":
//                MembreBureau membreBureau = membreBureauSerive.
//                List<Evenement> adherentEvenements = adherentService.getListOfAdherentEvents(adherent);
//
//                if (adherentEvenements == null) {
//                    adherentEvenements = new ArrayList<>();
//                }
//
//                model.addAttribute("adherentEvenements", adherentEvenements);
//                break;
//
//        }

        model.addAttribute("user", user);
        model.addAttribute("evenements",evenements);
        return "calendrier";
    }


    @GetMapping("/clubs/{nom}")
    public String voirClub(@PathVariable String nom, Model model, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        Club club = clubRepository.findByNom(nom);
        model.addAttribute("club", club);

        // Vérifier si l'utilisateur est déjà adhérent
        boolean isAlreadyMember = false;
            if (user instanceof Adherent) {
                Adherent adherent = (Adherent) user;
                isAlreadyMember = club.getAdherents().stream()
                        .anyMatch(a -> a.getId().equals(adherent.getId()));
            }

        model.addAttribute("isAlreadyMember", isAlreadyMember);
        model.addAttribute("user", user);

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
                MembreBureau membre = membreBureauSerive.getMembreFromUser(user) ;
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

    @GetMapping("/clubs/demander-creation")
    public String showClubCreationForm(Model model, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        model.addAttribute("user", user);
        return "demander-creation";
    }

    @PostMapping("/clubs/demander-creation")
    public String createClubRequest(@RequestParam String nom,
                                    @RequestParam String description,
                                    @RequestParam String objectifs,
                                    @RequestParam List<String> emails,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");

        for (String email : emails) {
            Utilisateur membre = utilisateurRepository.findByEmail(email);
            if (membre == null) {
                redirectAttributes.addFlashAttribute("message",
                        "L'email " + email + " n'existe pas dans la base !");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/clubs/demander-creation";
            }
        }

        DemandeClub demande = new DemandeClub();
        demande.setNom(nom);
        demande.setDescription(description);
        demande.setObjectifs(objectifs);
        demande.setDemandeur(user);
        demande.setStatut("EN_ATTENTE");

        demande.setEmailsMembres(emails);
        demandeClubRepository.save(demande);

        redirectAttributes.addFlashAttribute("message",
                "Votre demande de création du club '" + nom + "' a été soumise avec succès ! Elle sera examinée par un administrateur.");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/mes-clubs";
    }

    @GetMapping("/gestion/demandes")
    public String showDemandesPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            redirectAttributes.addFlashAttribute("message", "Accès refusé.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/";
        }

        List<DemandeAdhesion> demandes = demandeAdhesionService.findByStatut("EN_ATTENTE");
        model.addAttribute("demandesEnAttente", demandes);

        // Gérer les messages flash (si une demande vient d'être acceptée/refusée)
        if (session.getAttribute("message") != null) {
            model.addAttribute("message", session.getAttribute("message"));
            model.addAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }

        return "gestion-demandes";
    }



}
