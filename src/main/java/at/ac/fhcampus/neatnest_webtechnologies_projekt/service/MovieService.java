package at.ac.fhcampus.neatnest_webtechnologies_projekt.service;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Movie;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.MovieRepository;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired // -> zugriff auf die Tabelle. Entweder wird Objekt zürckgegeben oder wir fügen was in die DB hinzu und Repo kümmert sich dann um DML
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;


    public User getUserFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userRepository.findById(userId).orElse(null);

        // wir holen die SessionID vom RAM raus bei einem eingeloggten User raus. Und Speichern
        // es in Long ein. getAttribute gibt immer dann ein Objekt zurück deswegen müssen wir es casten.
        // UserID ist einfach die Speicherung im RAM. Spalte.
        //gibt in dem Fall die ID aus Model zurück mit der wir eine Session ID einordnen

    }

    // Alle Filme des Users laden
    public List<Movie> getAllMovies( HttpSession session) {
        //HttpSession session : Spring sieht das Cookie, sucht die Session im RAM und gibt sie uns fertig als HttpSession Objekt
        User user = getUserFromSession(session);
        return movieRepository.findByUser(user);
    }


    public Movie addMovie(HttpSession session, Movie movie) {
        User user = getUserFromSession(session);
        if (movieRepository.existsByUserAndTitleIgnoreCase(user, movie.getTitle())){
            return null;
        }
        movie.setUser(user);
        return movieRepository.save(movie);
    }


    public Movie updateMovie(HttpSession session, Long id, Movie updatedMovie) {
        User user = getUserFromSession(session);
        //holt den eingeloggten User durch SessionID ein gibt in dem fall ein ID aus model für einen User aus siehe oben!

        Optional<Movie> existing = movieRepository.findByIdAndUser(id, user);

        if (existing.isEmpty()) {
            return null;
        }

        Movie movie = existing.get();
        movie.setTitle(updatedMovie.getTitle());
        movie.setYear(updatedMovie.getYear());
        movie.setGenre(updatedMovie.getGenre());
        movie.setDirector(updatedMovie.getDirector());
        movie.setPosterUrl(updatedMovie.getPosterUrl());
        movie.setImdbId(updatedMovie.getImdbId());
        movie.setReleased(updatedMovie.getReleased());
        movie.setRuntime(updatedMovie.getRuntime());
        movie.setActors(updatedMovie.getActors());
        movie.setPlot(updatedMovie.getPlot());
        movie.setImdbRating(updatedMovie.getImdbRating());
        return movieRepository.save(movie);
    }

    // Film löschen
    public boolean deleteMovie(HttpSession session, Long id) {
        User user = getUserFromSession(session);
        Optional<Movie> existing = movieRepository.findByIdAndUser(id, user);

        if (existing.isEmpty()) {
            return false;
        }

        movieRepository.delete(existing.get());
        return true;
    }

    // Filme nach Titel suchen
    public List<Movie> searchMovies(HttpSession session, String query) {
        User user = getUserFromSession(session);
        return movieRepository.findByUserAndTitleContainingIgnoreCase(user, query);
    }
}