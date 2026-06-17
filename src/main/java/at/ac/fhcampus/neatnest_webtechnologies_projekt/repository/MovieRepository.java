package at.ac.fhcampus.neatnest_webtechnologies_projekt.repository;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Movie;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    //Methoden die wir implementieren tuen.
    //Filme eines bestimmten users anzeigen, die sich in der DB befinden
    List<Movie> findByUser(User user);

    // Gibt einen einzigen Film anhand von ID aus oder halt nix. Optional weil kann leer sein
    Optional<Movie> findByIdAndUser(Long id, User user);

    // Filme nach Titel suchen
    List<Movie> findByUserAndTitleContainingIgnoreCase(User user, String title);

    boolean existsByUserAndTitleIgnoreCase(User user, String title);
}