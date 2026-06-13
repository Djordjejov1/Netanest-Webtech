package at.ac.fhcampus.neatnest_webtechnologies_projekt.repository;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Favorite;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Alle Favoriten eines Users laden
    List<Favorite> findByUser(User user);

    // Einen Favoriten per ID und User finden (Sicherheit!)
    Optional<Favorite> findByIdAndUser(Long id, User user);

    // Alle abgelaufenen Favoriten finden (für den Auto-Delete Job)
    List<Favorite> findByExpiresAtBefore(LocalDateTime now);

    // Prüfen ob dieser Favorit bereits existiert
    Optional<Favorite> findByUserAndMediaTypeAndMediaId(User user, String mediaType, Long mediaId);
}