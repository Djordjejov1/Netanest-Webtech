package at.ac.fhcampus.neatnest_webtechnologies_projekt.controller;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Favorite;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // GET /api/favorites → alle Favoriten des Users laden
    @GetMapping
    public ResponseEntity getAllFavorites(HttpSession session) {
        List<Favorite> favorites = favoriteService.getAllFavorites(session);
        return ResponseEntity.ok(favorites);
    }

    // POST /api/favorites → neuen Favoriten hinzufügen
    @PostMapping
    public ResponseEntity addFavorite(HttpSession session, @RequestBody Favorite favorite) {
        Favorite saved = favoriteService.addFavorite(session, favorite);
        if (saved == null) {
            return ResponseEntity.status(409).body("Bereits als Favorit gespeichert");
        }
        return ResponseEntity.status(201).body(saved);
    }

    // DELETE /api/favorites/{id} → Favoriten manuell löschen
    @DeleteMapping("/{id}")
    public ResponseEntity deleteFavorite(HttpSession session, @PathVariable Long id) {
        boolean deleted = favoriteService.deleteFavorite(session, id);
        if (!deleted) return ResponseEntity.status(404).body("Favorit nicht gefunden");
        return ResponseEntity.ok("Favorit gelöscht");
    }
}