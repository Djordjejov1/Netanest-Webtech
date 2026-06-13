package at.ac.fhcampus.neatnest_webtechnologies_projekt.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExternalBookClient {

    //Wir haben Open Library statt Google Books verwendet,
    //da Google Books ohne API Key ein Tageslimit hat das für eine Demo unzuverlässig ist
    private static final String API_URL = "https://openlibrary.org";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/";

    public List<Map> searchBooks(String query) {
        try {
            Map response = WebClient.create(API_URL)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search.json")
                            .queryParam("q", query)
                            .queryParam("limit", 10)
                            .queryParam("fields", "title,author_name,first_publish_year,isbn,cover_i")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // "docs" enthält die Bücher
            List<Map> docs = (List<Map>) response.get("docs");
            if (docs == null) return new ArrayList<>();

            List<Map> books = new ArrayList<>();
            for (Map doc : docs) {
                Map<String, Object> book = new HashMap<>();
                book.put("title", doc.get("title"));

                // Autor ist eine Liste → erstes Element nehmen
                List<String> authors = (List<String>) doc.get("author_name");
                book.put("author", authors != null && !authors.isEmpty() ? authors.get(0) : "Unbekannt");

                // Erscheinungsjahr
                Object year = doc.get("first_publish_year");
                book.put("year", year != null ? year.toString() : "");

                // ISBN
                List<String> isbns = (List<String>) doc.get("isbn");
                book.put("isbn", isbns != null && !isbns.isEmpty() ? isbns.get(0) : "");

                // Cover-Bild
                Object coverId = doc.get("cover_i");
                book.put("thumbnail", coverId != null ? COVER_URL + coverId + "-M.jpg" : "");

                books.add(book);
            }

            return books;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
