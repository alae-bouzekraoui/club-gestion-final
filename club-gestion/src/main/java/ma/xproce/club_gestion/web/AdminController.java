package ma.xproce.club_gestion.web;

import jakarta.servlet.http.HttpSession;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/signin";

        model.addAttribute("user", user);
        model.addAttribute("clubs", adminService.getAllClubs());
        return "admin-dashboard";
    }

    @GetMapping("/admin/demandes")
    public String viewDemandes(Model model, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/signin";
        }

        model.addAttribute("demandes", adminService.getDemandesEnAttente());
        model.addAttribute("user", user);

        return "admin-demandes";
    }

    @PostMapping("/admin/demandes/{id}/accepter")
    public String accepterDemande(@PathVariable Long id, @RequestParam(required = false) String poste, RedirectAttributes ra) {
        adminService.accepterDemande(id, poste);
        ra.addFlashAttribute("message", "Club accepté avec succès !");
        ra.addFlashAttribute("messageType", "success");
        return "redirect:/admin/demandes";
    }

    @PostMapping("/admin/demandes/{id}/refuser")
    public String refuserDemande(@PathVariable Long id, @RequestParam String commentaire, RedirectAttributes ra) {
        adminService.refuserDemande(id, commentaire);
        ra.addFlashAttribute("message", "Demande refusée.");
        ra.addFlashAttribute("messageType", "warning");
        return "redirect:/admin/demandes";
    }

    @PostMapping("/admin/clubs/{id}/delete")
    public String deleteClub(@PathVariable Long id, RedirectAttributes ra) {
        adminService.deleteClub(id);
        ra.addFlashAttribute("message", "Club supprimé.");
        return "redirect:/admin/dashboard";
    }
}
