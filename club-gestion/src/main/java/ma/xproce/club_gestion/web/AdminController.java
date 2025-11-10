package ma.xproce.club_gestion.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final ClubRepository clubRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/signin";
        }
        model.addAttribute("user", user);
        model.addAttribute("clubs", clubRepository.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/admin/demandes")
    public String demandes(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/signin";
        }
        model.addAttribute("user", user);
        return "admin-demandes";
    }

    @PostMapping("/admin/add-club")
    public String addClub(@ModelAttribute Club club) {
        clubRepository.save(club);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/clubs/{id}/delete")
    public String deleteClub(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clubRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Club supprimé avec succès !");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/clubs/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));
        model.addAttribute("club", club);
        return "edit-club"; // 👉 nom du fichier HTML (page d'édition)
    }

    @PostMapping("/admin/clubs/{id}/edit")
    public String updateClub(@PathVariable Long id, @ModelAttribute Club updatedClub, RedirectAttributes redirectAttributes) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        // 🧩 Met à jour les champs
        club.setNom(updatedClub.getNom());
        club.setDescription(updatedClub.getDescription());
        club.setObjectifs(updatedClub.getObjectifs());
        club.setDateCreation(updatedClub.getDateCreation());

        clubRepository.save(club);

        redirectAttributes.addFlashAttribute("message", "✅ " + club.getNom() + " a été mis à jour avec succès !");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/admin/dashboard";
    }



}
