package ma.xproce.club_gestion.web;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.dao.repositories.*;
import ma.xproce.club_gestion.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UtilisateurService utilisateurService;
    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AdherentService adherentService;
    private final MembreBureauService membreBureauService;
    private final EvenementService evenementService;
    private final DemandeClubRepository demandeClubRepository;
    private final DemandeAdhesionService demandeAdhesionService;
    private final ClubService clubService;


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


    @PostMapping("/clubs/{clubId}/adhesion")
    public String creerDemandeAdhesion(@PathVariable Long clubId,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {

        Club club = clubService.getClubById(clubId);
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        try {
            demandeAdhesionService.ajouterDemandeAdhesion(
                    clubId,
                    user
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

        return "redirect:/clubs/" + club.getNom();
    }


    @GetMapping("/calendrier")
    public String calendrier(HttpSession session, Model model) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        model.addAttribute("role", user.getRole());
        List<Evenement> evenements = new ArrayList<>();

        switch (user.getRole()) {

            case "ADHERENT":
                Adherent adherent = adherentService.getAdherentFromUser(user);
                if (adherent != null) {
                    evenements = adherentService.getListOfAdherentEvents(adherent);
                }
                break;

            case "MembreBureau":
                MembreBureau mb = membreBureauService.getMembreFromUser(user);
                if (mb != null) {
                    evenements = membreBureauService.getListOfEventsForMembre(mb);
                }
                break;
        }

        if (evenements == null) evenements = new ArrayList<>();

        model.addAttribute("user", user);
        model.addAttribute("evenements", evenements);

        return "calendrier";
    }

    @GetMapping("/clubs/{clubNom}")
    public String showClubDetails(@PathVariable String clubNom, Model model, HttpSession session) {

        Club club = clubService.getClubByNom(clubNom); // ou getClubById
        model.addAttribute("club", club);
        model.addAttribute("clubName", club.getNom());

        Utilisateur user = (Utilisateur) session.getAttribute("user");

        boolean isAlreadyMember = false;
        boolean hasPendingRequest = false;

        if (user != null) {
            isAlreadyMember = adherentService.isUserMember(user.getId(), club.getId());

            hasPendingRequest = demandeAdhesionService.hasPendingRequest(user.getId(), club.getId());
        }

        model.addAttribute("isAlreadyMember", isAlreadyMember);
        model.addAttribute("hasPendingRequest", hasPendingRequest);

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
                MembreBureau membre = membreBureauService.getMembreFromUser(user) ;
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
            RedirectAttributes redirectAttributes) {

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
            HttpSession session){
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            return "redirect:/signin";
        }
        Adherent adherent = adherentService.getAdherentFromUser(user);
        boolean added = evenementService.ajoutEvenementCalendrier(id, adherent);

        if (!added) {
            redirectAttributes.addFlashAttribute("message", "Cet événement est déjà dans votre calendrier.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
        } else {
            redirectAttributes.addFlashAttribute("message", "Événement ajouté à votre calendrier !");
            redirectAttributes.addFlashAttribute("messageType", "success");
        }

        return "redirect:/evenements";

    }


    @PostMapping("/evenements/supprimer_du_calendrier/{id}")
    public String removeEventFromCalendar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        Adherent adherent = adherentService.getAdherentFromUser(user);
        boolean removed = evenementService.removeEventFromCalendar(id, adherent);

        redirectAttributes.addFlashAttribute("message", "Événement retiré !");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/calendrier";
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


        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "Veuillez vous connecter.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/signin";
        }

        MembreBureau membreBureau = membreBureauService.getMembreFromUser(user);
        if (membreBureau == null) {
            redirectAttributes.addFlashAttribute("message", "Accès non autorisé. Réservé aux membres du bureau.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/";
        }


        List<Club> managedClubs = membreBureau.getClubList();

        if (managedClubs == null || managedClubs.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Erreur : Vous n'êtes associé à aucun club.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/";
        }

        List<DemandeAdhesion> demandes = demandeAdhesionService.getPendingRequestsForClubs(managedClubs);

        model.addAttribute("demandesEnAttente", demandes);

        model.addAttribute("managedClubs", managedClubs);

        return "gestion-demandes";
    }

    @PostMapping("/gestion/demandes/{demandeAdhesionId}/accepter")
    public String accepterDemandeAdhesion(@PathVariable Long demandeAdhesionId,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes){

        demandeAdhesionService.accepterDemandeAdhesion(demandeAdhesionId);
        redirectAttributes.addFlashAttribute("message",
                "Vous avez accépté la demande ");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/gestion/demandes";
    }

    @PostMapping("/gestion/demandes/{demandeAdhesionId}/refuser")
    public String refuserDemandeAdhesion(@PathVariable Long demandeAdhesionId,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes){

        demandeAdhesionService.refuserDemandeAdhesion(demandeAdhesionId);
        redirectAttributes.addFlashAttribute("message",
                "Vous avez refusé la demande ");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/gestion/demandes";
    }


}
