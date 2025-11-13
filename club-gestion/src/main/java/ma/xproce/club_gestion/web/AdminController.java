package ma.xproce.club_gestion.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import ma.xproce.club_gestion.dao.entities.Club;
import ma.xproce.club_gestion.dao.entities.DemandeClub;
import ma.xproce.club_gestion.dao.entities.MembreBureau;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.dao.repositories.ClubRepository;
import ma.xproce.club_gestion.dao.repositories.DemandeClubRepository;
import ma.xproce.club_gestion.dao.repositories.MembreBureauRepository;
import ma.xproce.club_gestion.dao.repositories.UtilisateurRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreBureauRepository membreBureauRepository;
    private final DemandeClubRepository demandeClubRepository;


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


    @PostMapping("/admin/clubs/{id}/delete")
    public String deleteClub(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clubRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Club supprimé avec succès !");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/admin/dashboard";
    }

//    @GetMapping("/admin/clubs/{id}/edit")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        Club club = clubRepository.findById(id).orElse(null);
//        if (club == null) {
//            return "redirect:/admin/dashboard";
//        }
//        model.addAttribute("club", club);
//        return "edit-club";
//    }
//
//    @PostMapping("/admin/clubs/{id}/edit")
//    public String updateClub(
//            @PathVariable Long id,
//            @ModelAttribute Club updatedClub,
//            @RequestParam(required = false) List<String> emails,
//            @RequestParam(required = false) List<String> postes,
//            RedirectAttributes redirectAttributes
//    ) {
//        Club club = clubRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Club introuvable"));
//
//        // ✅ Mettre à jour les infos du club
//        club.setNom(updatedClub.getNom());
//        club.setDescription(updatedClub.getDescription());
//        club.setObjectifs(updatedClub.getObjectifs());
//        club.setDateCreation(updatedClub.getDateCreation());
//
//        // ✅ Gestion des membres du bureau
//        List<MembreBureau> membresActuels = new ArrayList<>(club.getMembreBureauList());
//
//        // 1️⃣ Supprimer les anciens membres qui ne sont plus dans la nouvelle liste
//        for (MembreBureau ancienMembre : membresActuels) {
//            boolean existeEncore = false;
//            if (emails != null) {
//                for (String email : emails) {
//                    if (ancienMembre.getEmail().equalsIgnoreCase(email)) {
//                        existeEncore = true;
//                        break;
//                    }
//                }
//            }
//
//            if (!existeEncore) {
//                ancienMembre.getClubList().remove(club);
//                club.getMembreBureauList().remove(ancienMembre);
//                membreBureauRepository.save(ancienMembre);
//            }
//        }
//
//        if (emails != null) {
//            for (int i = 0; i < emails.size(); i++) {
//                String email = emails.get(i).trim();
//                String poste = postes.get(i).trim();
//
//                Utilisateur user = utilisateurRepository.findByEmail(email);
//                MembreBureau membre = membreBureauRepository.getById(user.getId());
//
//                if (membre != null) {
//                    membre.setPoste(poste);
//                    if (membre.getClubList() == null) membre.setClubList(new ArrayList<>());
//                    if (!membre.getClubList().contains(club)) {
//                        membre.getClubList().add(club);
//                    }
//                    membreBureauRepository.save(membre);
//
//                    if (!club.getMembreBureauList().contains(membre)) {
//                        club.getMembreBureauList().add(membre);
//                    }
//                } else {
//                    if (user == null) {
//                        redirectAttributes.addFlashAttribute("error", "L'email " + email + " n'existe pas !");
//                        return "redirect:/admin/clubs/" + id + "/edit";
//                    }
//
//                    MembreBureau nouveauMembre = new MembreBureau();
//                    nouveauMembre.setNom(user.getNom());
//                    nouveauMembre.setPrenom(user.getPrenom());
//                    nouveauMembre.setEmail(user.getEmail());
//                    nouveauMembre.setMotDePasse(user.getMotDePasse());
//                    nouveauMembre.setRole("MembreBureau");
//                    nouveauMembre.setPoste(poste);
//
//                    utilisateurRepository.delete(user);
//
//                    nouveauMembre.setClubList(new ArrayList<>(List.of(club)));
//                    membreBureauRepository.save(nouveauMembre);
//
//                    club.getMembreBureauList().add(nouveauMembre);
//                }
//            }
//        }
//
//        clubRepository.save(club);
//
//        redirectAttributes.addFlashAttribute("message", "✅ Le club " + club.getNom() + " a été mis à jour avec succès !");
//        redirectAttributes.addFlashAttribute("messageType", "success");
//
//        return "redirect:/admin/dashboard";
//    }

    @GetMapping("/admin/demandes")
    public String viewDemandes(Model model, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/signin";
        }

        List<DemandeClub> demandes = demandeClubRepository.findByStatut("EN_ATTENTE");
        model.addAttribute("demandes", demandes);
        model.addAttribute("user", user);

        return "admin-demandes";
    }

    @PostMapping("/admin/demandes/{id}/accepter")
    public String accepterDemande(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        DemandeClub demande = demandeClubRepository.findById(id).orElse(null);
        if (demande == null) return "redirect:/admin/demandes";

        // Créer le club
        Club club = new Club();
        club.setNom(demande.getNom());
        club.setDescription(demande.getDescription());
        club.setObjectifs(demande.getObjectifs());
        clubRepository.save(club);

        List<String> emails = demande.getEmailsMembres() == null
                ? Collections.emptyList()
                : demande.getEmailsMembres();

        for (String email : emails) {

            if (email == null || email.isBlank()) continue;

            Utilisateur user = utilisateurRepository.findByEmail(email.trim());
            if (user == null) continue;

            // Si déjà membre du bureau → juste lui ajouter le club
            if (user instanceof MembreBureau membreBureau) {
                if (membreBureau.getClubList() == null)
                    membreBureau.setClubList(new ArrayList<>());

                if (membreBureau.getClubList().stream().noneMatch(c -> c.getId().equals(club.getId()))) {
                    membreBureau.getClubList().add(club);
                    membreBureauRepository.save(membreBureau);
                }
                continue;
            }

            // Sinon : on transforme le user en membre du bureau
            user.setRole("MembreBureau");
            utilisateurRepository.save(user);

            // Récupérer le MembreBureau généré (car single table inheritance)
            MembreBureau membre = membreBureauRepository.findById(user.getId()).orElse(null);

            if (membre != null) {
                if (membre.getClubList() == null)
                    membre.setClubList(new ArrayList<>());

                membre.getClubList().add(club);
                membreBureauRepository.save(membre);
            }
        }

        // ✔ Supprimer la demande une seule fois, à la fin
        demandeClubRepository.delete(demande);

        redirectAttributes.addFlashAttribute("message",
                "Le club '" + demande.getNom() + "' a été accepté !");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/admin/demandes";
    }


    @PostMapping("/admin/demandes/{id}/refuser")
    public String refuserDemande(@PathVariable Long id,
                                 @RequestParam(required = false) String commentaire,
                                 RedirectAttributes redirectAttributes) {
        DemandeClub demande = demandeClubRepository.findById(id).orElse(null);

        if (demande != null) {
            demande.setStatut("REFUSEE");
            demande.setCommentaireAdmin(commentaire);
            demandeClubRepository.save(demande);

            redirectAttributes.addFlashAttribute("message",
                    "❌ La demande pour le club '" + demande.getNom() + "' a été refusée.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
        }

        return "redirect:/admin/demandes";
    }
}
