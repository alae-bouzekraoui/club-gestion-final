package ma.xproce.club_gestion.web;

import jakarta.servlet.http.HttpSession;
import ma.xproce.club_gestion.dao.entities.*;
import ma.xproce.club_gestion.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private AdherentService adherentService;
    @Autowired
    private IMembreBureauService membreBureauService;
    @Autowired
    private EvenementService evenementService;
    @Autowired
    private DemandeAdhesionService demandeAdhesionService;
    @Autowired
    private IClubService clubService;


    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Club> clubs = clubService.getAllClubs();
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
            model.addAttribute("error", e.getMessage());
            return "signin";
        }
    }


    @PostMapping("/signup")
    public String registerUser(@ModelAttribute Utilisateur user, HttpSession session, Model model) {
        try {
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

        List<Club> mesClubs = adherentService.getClubsByAdherent(user.getId());

        model.addAttribute("clubs", mesClubs);
        model.addAttribute("user", user);
        return "mes-clubs";
    }


    @PostMapping("/clubs/{clubId}/adhesion")
    public String adhererClub(@PathVariable("clubId") Long clubId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        try {
            Utilisateur updatedUser = clubService.rejoindreClub(clubId, user.getId());

            // ✅ On met à jour la session avec l'objet tout frais (qui contient le bon rôle et le bon ID)
            session.setAttribute("user", updatedUser);

            redirectAttributes.addFlashAttribute("message", "Félicitations, vous avez rejoint le club !");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "warning");
        }

        return "redirect:/";
    }

    @PostMapping("/clubs/{clubId}/desadhesion")
    public String desadhererClub(@PathVariable("clubId") Long clubId, HttpSession session, RedirectAttributes redirectAttributes) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        try {
            Utilisateur updatedUser = clubService.quitterClub(clubId, user.getId());
            session.setAttribute("user", updatedUser);

            redirectAttributes.addFlashAttribute("message", "Vous avez quitté le club avec succès.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/mes-clubs";
    }

    @PostMapping("/clubs/{clubId}/demande-adhesion")
    public String creerDemandeAdhesion(@PathVariable("clubId") Long clubId,
                                       @RequestParam("nom") String nom,
                                       @RequestParam("description") String description,
                                       @RequestParam("objectifs") String objectifs,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "Vous devez être connecté.");
            return "redirect:/signin";
        }

        try {
            demandeAdhesionService.creerDemandeAdhesion(nom, description, objectifs, clubId, user.getId());

            redirectAttributes.addFlashAttribute("message", "Votre demande a été envoyée !");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/clubs/" + clubId;
    }


    @GetMapping("/calendrier")
    public String calendrier(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) return "redirect:/signin";

        List<Evenement> evenements = evenementService.getEvenementsPourUtilisateur(user);

        model.addAttribute("user", user);
        model.addAttribute("role", user.getRole());
        model.addAttribute("evenements", evenements);

        return "calendrier";
    }


    @GetMapping("/clubs/{nom}")
    public String voirClub(@PathVariable String nom, Model model, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        Club club = clubService.getClubParNom(nom);

        boolean isAlreadyMember = clubService.estAdherentDuClub(user, club);

        model.addAttribute("club", club);
        model.addAttribute("isAlreadyMember", isAlreadyMember);
        model.addAttribute("user", user);

        return "club-details";
    }


    @GetMapping("/evenements")
    public String showEvenements(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "Veuillez vous connecter.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/signin";
        }

        List<Evenement> evenements = evenementService.getEvenementsPourUtilisateur(user);

        model.addAttribute("evenements", evenements);
        model.addAttribute("user", user);
        model.addAttribute("role", user.getRole());

        return "evenements";
    }


    @PostMapping("/evenements/add")
    public String addEvenement(
            @ModelAttribute Evenement evenement,
            @RequestParam("clubId") Long clubId,
            RedirectAttributes redirectAttributes) {

        try {
            evenementService.ajouterEvenement(clubId, evenement);
            redirectAttributes.addFlashAttribute("message", "Événement ajouté avec succès !");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "Erreur : " + e.getMessage());
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
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/evenements";
    }


    @PostMapping("/evenements/Ajout_au_calendrier/{id}")
    public String ajoutEvenemetCalendrier(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            return "redirect:/signin";
        }

        try {
            boolean added = evenementService.ajoutEvenementCalendrier(id, user.getId());

            if (!added) {
                redirectAttributes.addFlashAttribute("message", "Cet événement est déjà dans votre calendrier.");
                redirectAttributes.addFlashAttribute("messageType", "warning");
            } else {
                redirectAttributes.addFlashAttribute("message", "Événement ajouté à votre calendrier !");
                redirectAttributes.addFlashAttribute("messageType", "success");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur : " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/evenements";
    }

    @PostMapping("/evenements/supprimer_du_calendrier/{id}")
    public String removeEventFromCalendar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        try {
            evenementService.retirerEvenementDuCalendrier(id, user.getId());
            redirectAttributes.addFlashAttribute("message", "Événement retiré avec succès !");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur lors du retrait : " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/calendrier";
    }


    @GetMapping("/clubs/demander-creation")
    public String showClubCreationForm(Model model, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        model.addAttribute("user", user);
        return "demander-creation";
    }


    @PostMapping("/clubs/demander-creation")
    public String createClubRequest(@ModelAttribute DemandeClub demande,
                                    @RequestParam("emails") List<String> emails,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        try {
            clubService.soumettreDemandeCreation(demande, emails, user.getId());
            redirectAttributes.addFlashAttribute("message", "Votre demande pour le club '" + demande.getNom() + "' a été soumise avec succès !");
            redirectAttributes.addFlashAttribute("messageType", "success");

            return "redirect:/mes-clubs";

        } catch (RuntimeException e) {
            model.addAttribute("demande", demande);
            model.addAttribute("emailsSaisis", emails);
            model.addAttribute("message", "Erreur : " + e.getMessage());
            model.addAttribute("messageType", "danger");
            model.addAttribute("user", user);

            return "demander-creation";
        }
    }

    @GetMapping("/gestion/demandes")
    public String showDemandesPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        // 1. Vérification de sécurité (Idéalement, vérifier si le rôle est ADMIN ou RESPONSABLE)
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "Accès refusé.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/";
        }

        List<DemandeAdhesion> demandes = demandeAdhesionService.getDemandesParStatut("EN_ATTENTE");
        model.addAttribute("demandesEnAttente", demandes);
        model.addAttribute("user", user);

        return "gestion-demandes";
    }
}
