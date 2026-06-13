package at.ac.fhcampus.neatnest_webtechnologies_projekt.controller;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Movie;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;


    @GetMapping
    public ResponseEntity getAllMovies(HttpSession session) {
        List<Movie> movies = movieService.getAllMovies(session);
        return ResponseEntity.ok(movies);
    }


    @PostMapping
    public ResponseEntity addMovie(HttpSession session, @RequestBody Movie movie) {
        Movie saved = movieService.addMovie(session, movie);
        return ResponseEntity.status(201).body(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity updateMovie(HttpSession session, @PathVariable Long id, @RequestBody Movie movie) {
        Movie updated = movieService.updateMovie(session, id, movie);
        if (updated == null) {
            return ResponseEntity.status(404).body("Film nicht gefunden");
        }
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity deleteMovie(HttpSession session, @PathVariable Long id) {
        boolean deleted = movieService.deleteMovie(session, id);
        if (!deleted) {
            return ResponseEntity.status(404).body("Film nicht gefunden");
        }
        return ResponseEntity.ok("Film gelöscht");
    }


    @GetMapping("/search")
    public ResponseEntity searchMovies(HttpSession session, @RequestParam String q) {
        List<Movie> movies = movieService.searchMovies(session, q);
        return ResponseEntity.ok(movies);
        //Suche erfolgt anhand von @RequestParam -> ?q Spring bearbeitet es automatisch.
        //Security erfolgt durch SecurityConfig Spring speert ja alles vom Werk.
    }
}