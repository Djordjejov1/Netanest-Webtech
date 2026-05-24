package at.ac.fhcampus.neatnest_webtechnologies_projekt.controller;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.client.ExternalMovieClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external")
public class ExternalController {

    @Autowired
    private ExternalMovieClient externalMovieClient;

    // OMDB Film Suche
    @GetMapping("/movies/search")
    public ResponseEntity searchMovies(@RequestParam String q) {
        List<Map> movies = externalMovieClient.searchMovies(q);
        return ResponseEntity.ok(movies);
    }
}