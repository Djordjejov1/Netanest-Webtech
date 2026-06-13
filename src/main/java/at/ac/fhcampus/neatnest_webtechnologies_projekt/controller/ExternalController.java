package at.ac.fhcampus.neatnest_webtechnologies_projekt.controller;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.client.ExternalBookClient;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.client.ExternalMovieClient;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.client.ExternalSongClient;
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

    @Autowired
    private ExternalBookClient externalBookClient;

    @Autowired
    private ExternalSongClient externalSongClient;

    // OMDB Film Suche
    @GetMapping("/movies/search")
    public ResponseEntity searchMovies(@RequestParam String q) {
        List<Map> movies = externalMovieClient.searchMovies(q);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/books/search")
    public ResponseEntity searchBooks(@RequestParam String q) {
        List<Map> books = externalBookClient.searchBooks(q);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/songs/search")
    public ResponseEntity searchSongs(@RequestParam String q) {
        List<Map> songs = externalSongClient.searchSongs(q);
        return ResponseEntity.ok(songs);
    }
}