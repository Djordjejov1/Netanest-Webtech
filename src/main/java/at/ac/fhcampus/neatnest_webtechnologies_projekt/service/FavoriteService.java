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

    public User getUserFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userRepository.findById(userId).orElse(null);
    }


    public List<Favorite> getAllFavorites(HttpSession session) {
        User user = getUserFromSession(session); // -> holt sich den eingeloggten User aus der Session
        return favoriteRepository.findByUser(user); //-> geht in db und holt alle Favoriten die zu diesem User gehören.
        //
    }


    public Favorite addFavorite(HttpSession session, Favorite favorite) {
        User user = getUserFromSession(session);


        Optional<Favorite> existing = favoriteRepository.findByUserAndMediaTypeAndMediaId(
                user, favorite.getMediaType(), favorite.getMediaId()
                //-> geht in DB & schaut ob dieser Favorit bereits exsistiert. Prüft anhand von den unten genannten sachen: user, favorite etc...
                // gibt Optional zurück kann leer sein, kann aber befüllt sein
        );

        if (existing.isPresent()) {
            return null; // isPresent() prüft einfach ob die optionale Liste leer ist oder nicht.
        }

        favorite.setUser(user); // setzt den eingeloggten benutzer auf das FAV-Objekt
        favorite.setExpiresAt(LocalDateTime.now().plusHours(1)); //Setzt den Ablaufzeitpunkt
        return favoriteRepository.save(favorite); // speichert den favoriten in db
    }


    public boolean deleteFavorite(HttpSession session, Long id) {
        User user = getUserFromSession(session);
        Optional<Favorite> existing = favoriteRepository.findByIdAndUser(id, user);

        if (existing.isEmpty()) return false;

        favoriteRepository.delete(existing.get());
        return true;
    }


    public void deleteExpiredFavorites() {
        List<Favorite> expired = favoriteRepository.findByExpiresAtBefore(LocalDateTime.now());
        for (Favorite favorite : expired) {
            favoriteRepository.delete(favorite);
        }
    }
}