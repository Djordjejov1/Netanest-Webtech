package at.ac.fhcampus.neatnest_webtechnologies_projekt.controller;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        // Username schon vergeben?
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.status(409).body("Username bereits vergeben");
        }

        // User erstellen und speichern
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user); // durch JPArepo erben wir alle vorgefertigte Methoden und können es ganz einfach abrufen!

        return ResponseEntity.status(201).body("Registrierung erfolgreich");
    }


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> body, HttpServletRequest request) {

        String username = body.get("username");
        String password = body.get("password");

        // User in DB suchen
        User user = userRepository.findByUsername(username).orElse(null);

        // User nicht gefunden oder Passwort falsch
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Ungültige Anmeldedaten");
        }

        // Session erstellen
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());

        return ResponseEntity.ok("Login erfolgreich");
    }


    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        // egal , ob eine Session exsitiert oder nicht. Er gibt dir hier einen Wert zurück

        if (session != null) {
            session.invalidate(); // sollte die Session vorhanden sein. Löscht Spring dann Session und eintrag aus dem RAM
        }

        return ResponseEntity.ok("Logout erfolgreich");
    }


    @GetMapping("/me")
    public ResponseEntity me(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(401).body("Nicht eingeloggt");
        }

        return ResponseEntity.ok("Eingeloggt als: " + session.getAttribute("username"));
    }
    // Wichtigster Part für die Authetifiztierung. BIn ich gerade eingeloggt.
    // Endpoint — jede Seite der App ruft das beim Laden auf um zu prüfen ob der User noch eingeloggt ist
}