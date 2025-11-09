package ma.xproce.club_gestion.web;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import ma.xproce.club_gestion.dao.entities.Utilisateur;
import ma.xproce.club_gestion.service.UtilisateurService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UtilisateurService utilisateurService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Liste de clubs temporaire
        List<String> clubs = List.of("Club Informatique", "Club Sportif", "Club Théâtre", "Club Musique");
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

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute Utilisateur user, HttpSession session) {
        // Enregistrer l’utilisateur dans la base
        utilisateurService.registerNewUser(user);

        // Le connecter directement après inscription
        session.setAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
