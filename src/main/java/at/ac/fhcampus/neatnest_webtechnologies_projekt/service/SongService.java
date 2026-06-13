package at.ac.fhcampus.neatnest_webtechnologies_projekt.service;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Song;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.SongRepository;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    public User getUserFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userRepository.findById(userId).orElse(null);
    }

    public List<Song> getAllSongs(HttpSession session) {
        User user = getUserFromSession(session);
        return songRepository.findByUser(user);
    }

    public Song addSong(HttpSession session, Song song) {
        User user = getUserFromSession(session);
        song.setUser(user);
        return songRepository.save(song);
    }

    public Song updateSong(HttpSession session, Long id, Song updatedSong) {
        User user = getUserFromSession(session);
        Optional<Song> existing = songRepository.findByIdAndUser(id, user);

        if (existing.isEmpty()) {
            return null;
        }

        Song song = existing.get();
        song.setTitle(updatedSong.getTitle());
        song.setArtist(updatedSong.getArtist());
        song.setAlbum(updatedSong.getAlbum());
song.setSpotifyUrl(updatedSong.getSpotifyUrl());
        song.setCoverUrl(updatedSong.getCoverUrl());
        song.setExplicit(updatedSong.getExplicit());
        song.setDuration(updatedSong.getDuration());
        song.setReleaseDate(updatedSong.getReleaseDate());

        return songRepository.save(song);
    }

    public boolean deleteSong(HttpSession session, Long id) {
        User user = getUserFromSession(session);
        Optional<Song> existing = songRepository.findByIdAndUser(id, user);

        if (existing.isEmpty()) {
            return false;
        }

        songRepository.delete(existing.get());
        return true;
    }

    public List<Song> searchSongs(HttpSession session, String query) {
        User user = getUserFromSession(session);
        return songRepository.findByUserAndTitleContainingIgnoreCase(user, query);
    }
}
