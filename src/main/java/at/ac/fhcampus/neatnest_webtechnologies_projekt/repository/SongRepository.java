package at.ac.fhcampus.neatnest_webtechnologies_projekt.repository;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Song;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByUser(User user);

    Optional<Song> findByIdAndUser(Long id, User user);

    List<Song> findByUserAndTitleContainingIgnoreCase(User user, String title);
}
