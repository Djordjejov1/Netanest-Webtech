package at.ac.fhcampus.neatnest_webtechnologies_projekt.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExternalSongClient {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private String cachedToken = null;
    private long tokenExpiresAt = 0;

    private String getAccessToken() {
        if (cachedToken == null || System.currentTimeMillis() > tokenExpiresAt) {

            String credentials = clientId + ":" + clientSecret;
            String encoded = java.util.Base64.getEncoder()
                    .encodeToString(credentials.getBytes());

            Map response = WebClient.create("https://accounts.spotify.com")
                    .post()
                    .uri("/api/token")
                    .header("Authorization", "Basic " + encoded)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("grant_type=client_credentials")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Hier wird token definiert ↓
            cachedToken = (String) response.get("access_token");
            tokenExpiresAt = System.currentTimeMillis() + 3500000;
        }
        return cachedToken;
    }

//    private String getAccessToken() {
//        // Client ID + Secret als Base64 kodieren
//
//        String credentials = clientId + ":" + clientSecret;
//        String encoded = java.util.Base64.getEncoder()
//                .encodeToString(credentials.getBytes());
//
//        Map response = WebClient.create("https://accounts.spotify.com")
//                .post()
//                .uri("/api/token")
//                .header("Authorization", "Basic " + encoded)
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .bodyValue("grant_type=client_credentials")
//                .retrieve()
//                .bodyToMono(Map.class)
//                .block();
//
//        return (String) response.get("access_token");
//    }

    public List<Map> searchSongs(String query){
        try {
            String token = getAccessToken();

            Map response = WebClient.create("https://api.spotify.com")
                    .get()
                    .uri("/v1/search?q=" + query + "&type=track&limit=10")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Spotify gibt zurück: tracks → items
            Map tracks = (Map) response.get("tracks");
            List<Map> items = (List<Map>) tracks.get("items");

            List<Map> songs = new ArrayList<>();
            for (Map item : items) {
                Map<String, Object> song = new HashMap<>();
                song.put("title", item.get("name"));

                // Artist ist eine Liste
                List<Map> artists = (List<Map>) item.get("artists");
                song.put("artist", artists.get(0).get("name"));

                // Album + Cover
                Map album = (Map) item.get("album");
                song.put("album", album.get("name"));

                List<Map> images = (List<Map>) album.get("images");
                song.put("coverUrl", images.get(0).get("url"));

                song.put("spotifyUrl",
                        ((Map) item.get("external_urls")).get("spotify"));


                //Explicit (true/false)
                song.put("explicit", item.get("explicit"));

                //Dauer in ms → umrechnen
                Integer durationMs = (Integer)
                        item.get("duration_ms");
                if (durationMs != null){
                    int minutes = durationMs / 60000;
                    int seconds = (durationMs % 60000) / 1000;
                    song.put("duration", minutes + ":" + String.format("%02d", seconds));
                }

                //Release Date aus dem Album
                song.put("releaseDate", album.get("release_date"));

                songs.add(song);
            }

            return songs;

        } catch (Exception e) {
            return new ArrayList<>();
        }

    }
}
