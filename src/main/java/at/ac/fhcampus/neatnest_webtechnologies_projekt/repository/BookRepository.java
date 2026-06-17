package at.ac.fhcampus.neatnest_webtechnologies_projekt.repository;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Book;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByUser(User user);

    Optional<Book> findByIdAndUser(Long id, User user);

    List<Book> findByUserAndTitleContainingIgnoreCase(User user, String title);

    boolean existsByUserAndTitleIgnoreCase(User user, String title);
}