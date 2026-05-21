package at.ac.fhcampus.neatnest_webtechnologies_projekt.repository;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    //User kann exsitieren muss er aber nicht deswegen Optional hier
    //Jpa ruft es im hintergrund auf und gleicht es ab.
    boolean existsByUsername(String username);
}