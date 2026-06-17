package at.ac.fhcampus.neatnest_webtechnologies_projekt.controller;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Song;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.service.SongService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping
    public ResponseEntity getAllSongs(HttpSession session) {
        List<Song> songs = songService.getAllSongs(session);
        return ResponseEntity.ok(songs);
    }

    @PostMapping
    public ResponseEntity addSong(HttpSession session, @RequestBody Song song) {
        Song saved = songService.addSong(session, song);
        if (saved == null) {
            return ResponseEntity.status(409).body("Bereits in deiner Liste");
        }
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateSong(HttpSession session, @PathVariable Long id, @RequestBody Song song) {
        Song updated = songService.updateSong(session, id, song);
        if (updated == null) {
            return ResponseEntity.status(404).body("Song nicht gefunden");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteSong(HttpSession session, @PathVariable Long id) {
        boolean deleted = songService.deleteSong(session, id);
        if (!deleted) {
            return ResponseEntity.status(404).body("Song nicht gefunden");
        }
        return ResponseEntity.ok("Song gelöscht");
    }

    @GetMapping("/search")
    public ResponseEntity searchSongs(HttpSession session, @RequestParam String q) {
        List<Song> songs = songService.searchSongs(session, q);
        return ResponseEntity.ok(songs);
    }
}
