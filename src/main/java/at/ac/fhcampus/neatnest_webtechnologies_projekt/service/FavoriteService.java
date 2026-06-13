package at.ac.fhcampus.neatnest_webtechnologies_projekt.service;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Favorite;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.FavoriteRepository;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    // User aus der Session holen (gleich wie bei Movie/Song/Book)
    public User getUserFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userRepository.findById(userId).orElse(null);
    }

    // Alle Favoriten des Users laden
    public List<Favorite> getAllFavorites(HttpSession session) {
        User user = getUserFromSession(session);
        return favoriteRepository.findByUser(user);
    }

    // Neuen Favoriten hinzufügen — TTL wird hier gesetzt!
    public Favorite addFavorite(HttpSession session, Favorite favorite) {
        User user = getUserFromSession(session);

        // Prüfen ob bereits vorhanden
        Optional<Favorite> existing = favoriteRepository.findByUserAndMediaTypeAndMediaId(
                user, favorite.getMediaType(), favorite.getMediaId()
        );

        if (existing.isPresent()) {
            return null; // bereits als Favorit gespeichert
        }

        favorite.setUser(user);
        favorite.setExpiresAt(LocalDateTime.now().plusHours(24));
        return favoriteRepository.save(favorite);
    }

    // Favoriten manuell löschen
    public boolean deleteFavorite(HttpSession session, Long id) {
        User user = getUserFromSession(session);
        Optional<Favorite> existing = favoriteRepository.findByIdAndUser(id, user);

        if (existing.isEmpty()) return false;

        favoriteRepository.delete(existing.get());
        return true;
    }

    // Abgelaufene Favoriten löschen — wird vom Scheduler aufgerufen
    public void deleteExpiredFavorites() {
        List<Favorite> expired = favoriteRepository.findByExpiresAtBefore(LocalDateTime.now());
        for (Favorite favorite : expired) {
            favoriteRepository.delete(favorite);
        }
    }



}