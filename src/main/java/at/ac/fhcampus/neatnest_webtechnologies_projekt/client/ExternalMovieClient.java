package at.ac.fhcampus.neatnest_webtechnologies_projekt.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ExternalMovieClient {

    @Value("${omdb.api.key}")
    private String apiKey;

    @Value("${omdb.api.url}")
    private String apiUrl;

    public List<Map> searchMovies(String query) {
        try {
            // OMDB API aufrufen
            Map response = WebClient.create(apiUrl)
                    .get()
                    .uri("/?s=" + query + "&apikey=" + apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("Search")) {
                return new ArrayList<>();
            }

            List<Map> results = (List<Map>) response.get("Search");
            List<Map> movies = new ArrayList<>();

            // Für jeden Film Details holen
            for (Map result : results) {
                String imdbId = (String) result.get("imdbID");
                Map details = getMovieDetails(imdbId);
                if (details != null) {
                    movies.add(details);
                }
            }

            return movies;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private Map getMovieDetails(String imdbId) {
        try {
            Map response = WebClient.create(apiUrl)
                    .get()
                    .uri("/?i=" + imdbId + "&apikey=" + apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) return null;

            // Nur relevante Felder zurückgeben
            Map<String, Object> movie = new java.util.HashMap<>();
            movie.put("imdbId", response.get("imdbID"));
            movie.put("title", response.get("Title"));
            movie.put("year", response.get("Year"));
            movie.put("genre", response.get("Genre"));
            movie.put("director", response.get("Director"));
            movie.put("posterUrl", response.get("Poster"));

            return movie;

        } catch (Exception e) {
            return null;
        }
    }
}