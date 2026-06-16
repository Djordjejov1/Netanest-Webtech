package at.ac.fhcampus.neatnest_webtechnologies_projekt.controller;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private AuthenticationManager authenticationManager;


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

        try {
            // Spring Security authentifiziert den User → ruft automatisch
            // userDetailsService() + passwordEncoder() aus SecurityConfig auf
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Security Context setzen — Spring weiß jetzt wer eingeloggt ist
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Session erstellen
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", userRepository.findByUsername(username).get().getId());
            session.setAttribute("username", username);

            return ResponseEntity.ok("Login erfolgreich");

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Ungültige Anmeldedaten");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        // false bedeutet, gib mir die bestehe Session, aber erstell keine neue wenn keine existiert
        // gib mir einen Hnull falls sich niemand eingeloggt hat

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



    //Accountdetails bearbeiten feature hinzugefügt:
    @GetMapping("/account")
    public ResponseEntity getAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ResponseEntity.status(401).body("Nicht eingeloggt");

        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User nicht gefunden");

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "createdAt", user.getCreatedAt().toString()
        ));
    }

    @PutMapping("/account/username")
    public ResponseEntity updateUsername(@RequestBody Map<String, String> body, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ResponseEntity.status(401).body("Nicht eingeloggt");

        String newUsername = body.get("username");
        if (newUsername == null || newUsername.isBlank()) return ResponseEntity.badRequest().body("Username darf nicht leer sein");

        if (userRepository.existsByUsername(newUsername)) return ResponseEntity.status(409).body("Username bereits vergeben");

        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElse(null);
        user.setUsername(newUsername);
        userRepository.save(user);
        session.setAttribute("username", newUsername);

        return ResponseEntity.ok("Username geändert");
    }

    @PutMapping("/account/password")
    public ResponseEntity updatePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ResponseEntity.status(401).body("Nicht eingeloggt");

        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElse(null);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(400).body("Aktuelles Passwort falsch");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Passwort geändert");
    }
}