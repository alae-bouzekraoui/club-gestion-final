package ma.xproce.club_gestion.web;

import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.Evenement;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.service.ClubService;
import ma.xproce.club_gestion.service.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/evenements")
public class EvenementController {

    @Autowired
    private EvenementService evenementService;

    @Autowired
    private ClubService clubService;

    /**
     * Affiche la page des événements
     */
    @GetMapping
    public String showEvenements(HttpSession session, Model model) {
        // Récupérer l'utilisateur connecté depuis la session
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/signin";
        }

        String role = user.getRole();
        model.addAttribute("user", user);
        model.addAttribute("role", role);
        model.addAttribute("activePage", "evenements");

        // Si c'est un ADHERENT
        if (role != null && role.equalsIgnoreCase("ADHERENT")) {
            List<Club> clubsAdheres = clubService.getClubsByAdherent(user.getId());
            model.addAttribute("clubsAdheres", clubsAdheres);
        }

        // Si c'est un MEMBREBUREAU
        if (role != null && role.equalsIgnoreCase("MEMBREBUREAU")) {
            List<Club> clubsMembreBureau = clubService.getClubsByMembreBureau(user.getId());
            model.addAttribute("clubsMembreBureau", clubsMembreBureau);
        }

        return "evenements";
    }

    /**
     * Ajouter un nouvel événement
     */
    @PostMapping("/add")
    public String addEvenement(
            @RequestParam Long clubId,
            @RequestParam String titre,
            @RequestParam String description,
            @RequestParam String date,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            if (user == null) {
                return "redirect:/signin";
            }

            // Vérifier que l'utilisateur est un MEMBREBUREAU (insensible à la casse)
            if (user.getRole() == null || !user.getRole().equalsIgnoreCase("MEMBREBUREAU")) {
                redirectAttributes.addFlashAttribute("messageType", "warning");
                redirectAttributes.addFlashAttribute("message", "Vous n'avez pas les droits pour ajouter un événement.");
                return "redirect:/evenements";
            }

            // Récupérer le club
            Club club = clubService.getClubById(clubId);
            if (club == null) {
                redirectAttributes.addFlashAttribute("messageType", "danger");
                redirectAttributes.addFlashAttribute("message", "Club non trouvé.");
                return "redirect:/evenements";
            }

            // Vérifier que l'utilisateur est bien membre du bureau du club (par ID)
            boolean isMb = club.getMembreBureauList().stream()
                    .anyMatch(m -> m.getId() != null && m.getId().equals(user.getId()));
            if (!isMb) {
                redirectAttributes.addFlashAttribute("messageType", "danger");
                redirectAttributes.addFlashAttribute("message", "Vous n'êtes pas autorisé à ajouter un événement pour ce club.");
                return "redirect:/evenements";
            }

            // Créer l'événement
            Evenement evenement = new Evenement();
            evenement.setTitre(titre);
            evenement.setDescription(description);
            evenement.setDate(LocalDate.parse(date));
            evenement.setClub(club);

            evenementService.createEvenement(evenement);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Événement créé avec succès !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "danger");
            redirectAttributes.addFlashAttribute("message", "Erreur lors de la création de l'événement : " + e.getMessage());
        }

        return "redirect:/evenements";
    }

    /**
     * Supprimer un événement
     */
    @PostMapping("/supprimer/{id}")
    public String deleteEvenement(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            if (user == null) {
                return "redirect:/signin";
            }

            // Vérifier que l'utilisateur est un MEMBREBUREAU (insensible à la casse)
            if (user.getRole() == null || !user.getRole().equalsIgnoreCase("MEMBREBUREAU")) {
                redirectAttributes.addFlashAttribute("messageType", "warning");
                redirectAttributes.addFlashAttribute("message", "Vous n'avez pas les droits pour supprimer un événement.");
                return "redirect:/evenements";
            }

            // Récupérer l'événement
            Evenement evenement = evenementService.getEvenementById(id);
            if (evenement == null) {
                redirectAttributes.addFlashAttribute("messageType", "danger");
                redirectAttributes.addFlashAttribute("message", "Événement non trouvé.");
                return "redirect:/evenements";
            }

            // Vérifier que l'utilisateur est bien membre du bureau du club associé
            Club club = evenement.getClub();
            boolean isMb = club.getMembreBureauList().stream()
                    .anyMatch(m -> m.getId() != null && m.getId().equals(user.getId()));
            if (!isMb) {
                redirectAttributes.addFlashAttribute("messageType", "danger");
                redirectAttributes.addFlashAttribute("message", "Vous n'êtes pas autorisé à supprimer cet événement.");
                return "redirect:/evenements";
            }

            // Supprimer l'événement
            evenementService.deleteEvenement(id);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Événement supprimé avec succès !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "danger");
            redirectAttributes.addFlashAttribute("message", "Erreur lors de la suppression de l'événement : " + e.getMessage());
        }

        return "redirect:/evenements";
    }

    /**
     * Ajouter un événement au calendrier (pour les adhérents)
     */
    @PostMapping("/Ajout_au_calendrier/{id}")
    public String ajouterAuCalendrier(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            if (user == null) {
                return "redirect:/signin";
            }

            // Cette fonctionnalité est probablement pour les adhérents
            if (!"ADHERENT".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("messageType", "warning");
                redirectAttributes.addFlashAttribute("message", "Vous n'êtes pas autorisé à ajouter cet événement au calendrier.");
                return "redirect:/evenements";
            }

            // Récupérer l'événement
            Evenement evenement = evenementService.getEvenementById(id);
            if (evenement == null) {
                redirectAttributes.addFlashAttribute("messageType", "danger");
                redirectAttributes.addFlashAttribute("message", "Événement non trouvé.");
                return "redirect:/evenements";
            }

            // Ajouter l'événement au calendrier de l'utilisateur
            evenementService.addEvenementToUserCalendar(user, evenement);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Événement ajouté à votre calendrier !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "danger");
            redirectAttributes.addFlashAttribute("message", "Erreur lors de l'ajout au calendrier : " + e.getMessage());
        }

        return "redirect:/evenements";
    }
}
