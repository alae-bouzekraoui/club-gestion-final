package ma.xproce.club_gestion.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.MembreBureau;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreBureauRepository membreBureauRepository;

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
    public String addClub(
            @ModelAttribute Club club,
            @RequestParam List<String> emails,
            @RequestParam List<String> postes,
            RedirectAttributes redirectAttributes
    ) {
        if (club.getMembreBureauList() == null) {
            club.setMembreBureauList(new ArrayList<>());
        }

        // Sauvegarder le club d'abord pour obtenir un ID
        clubRepository.save(club);

        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i).trim();
            String poste = postes.get(i).trim();

            // Vérifier que l'utilisateur existe
            Utilisateur user = utilisateurRepository.findByEmail(email);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "L'email " + email + " n'existe pas !");
                return "redirect:/admin/dashboard";
            }

            if ("MembreBureau".equalsIgnoreCase(user.getRole())) {

                MembreBureau membre = membreBureauRepository.getById(user.getId());

                if (!membre.getClubList().contains(club)) {
                    membre.getClubList().add(club);
                }

                if (!club.getMembreBureauList().contains(membre)) {
                    club.getMembreBureauList().add(membre);
                }

                membreBureauRepository.save(membre);
                clubRepository.save(club);

                continue;

            }

            // Créer un nouveau membreBureau avec les données de l'utilisateur existant
            MembreBureau membre = new MembreBureau();
            membre.setNom(user.getNom());
            membre.setPrenom(user.getPrenom());
            membre.setEmail(user.getEmail());
            membre.setMotDePasse(user.getMotDePasse());
            membre.setRole("MembreBureau");
            membre.setPoste(poste);

            // Initialiser la liste des clubs
            if (membre.getClubList() == null) {
                membre.setClubList(new ArrayList<>());
            }
            membre.getClubList().add(club);

            utilisateurRepository.delete(user);

            membre = membreBureauRepository.save(membre);

            club.getMembreBureauList().add(membre);
        }

        clubRepository.save(club);

        redirectAttributes.addFlashAttribute("success", "Club et membres ajoutés avec succès !");
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
        return "edit-club"; // Le nom du fichier HTML
    }

    @PostMapping("/admin/clubs/{id}/edit")
    public String updateClub(
            @PathVariable Long id,
            @ModelAttribute Club updatedClub,
            @RequestParam(required = false) List<String> emails,
            @RequestParam(required = false) List<String> postes,
            RedirectAttributes redirectAttributes
    ) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        // ✅ Mettre à jour les infos du club
        club.setNom(updatedClub.getNom());
        club.setDescription(updatedClub.getDescription());
        club.setObjectifs(updatedClub.getObjectifs());
        club.setDateCreation(updatedClub.getDateCreation());

        // ✅ Gestion des membres du bureau
        List<MembreBureau> membresActuels = new ArrayList<>(club.getMembreBureauList());

        // 1️⃣ Supprimer les anciens membres qui ne sont plus dans la nouvelle liste
        for (MembreBureau ancienMembre : membresActuels) {
            boolean existeEncore = false;
            if (emails != null) {
                for (String email : emails) {
                    if (ancienMembre.getEmail().equalsIgnoreCase(email)) {
                        existeEncore = true;
                        break;
                    }
                }
            }

            if (!existeEncore) {
                ancienMembre.getClubList().remove(club);
                club.getMembreBureauList().remove(ancienMembre);
                membreBureauRepository.save(ancienMembre);
            }
        }

        if (emails != null) {
            for (int i = 0; i < emails.size(); i++) {
                String email = emails.get(i).trim();
                String poste = postes.get(i).trim();

                Utilisateur user = utilisateurRepository.findByEmail(email);
                MembreBureau membre = membreBureauRepository.getById(user.getId());

                if (membre != null) {
                    membre.setPoste(poste);
                    if (membre.getClubList() == null) membre.setClubList(new ArrayList<>());
                    if (!membre.getClubList().contains(club)) {
                        membre.getClubList().add(club);
                    }
                    membreBureauRepository.save(membre);

                    if (!club.getMembreBureauList().contains(membre)) {
                        club.getMembreBureauList().add(membre);
                    }
                } else {
                    if (user == null) {
                        redirectAttributes.addFlashAttribute("error", "L'email " + email + " n'existe pas !");
                        return "redirect:/admin/clubs/" + id + "/edit";
                    }

                    MembreBureau nouveauMembre = new MembreBureau();
                    nouveauMembre.setNom(user.getNom());
                    nouveauMembre.setPrenom(user.getPrenom());
                    nouveauMembre.setEmail(user.getEmail());
                    nouveauMembre.setMotDePasse(user.getMotDePasse());
                    nouveauMembre.setRole("MembreBureau");
                    nouveauMembre.setPoste(poste);

                    utilisateurRepository.delete(user);

                    nouveauMembre.setClubList(new ArrayList<>(List.of(club)));
                    membreBureauRepository.save(nouveauMembre);

                    club.getMembreBureauList().add(nouveauMembre);
                }
            }
        }

        clubRepository.save(club);

        redirectAttributes.addFlashAttribute("message", "✅ Le club " + club.getNom() + " a été mis à jour avec succès !");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/admin/dashboard";
    }
}
