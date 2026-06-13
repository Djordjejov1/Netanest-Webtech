package at.ac.fhcampus.neatnest_webtechnologies_projekt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "release_year")
    private String year;

    private String genre;
    private String director;
    private String posterUrl;
    private String imdbId;
    private String released;
    private String runtime;
    private String actors;
    private String plot;
    private String imdbRating;


    //beziehung 1:n user hat mehrere Filme
    @ManyToOne //-> hängt von der Sicht ab in den fall mehrere filme gehören ztu einem user
    @JoinColumn(name = "user_id") // -> welche Columns werden verbunden ? naja pk -> fk
    private User user;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getImdbId() { return imdbId; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }

    public String getReleased() { return released; }
    public void setReleased(String released) { this.released = released; }

    public String getRuntime() { return runtime; }
    public void setRuntime(String runtime) { this.runtime = runtime; }

    public String getActors() { return actors; }
    public void setActors(String actors) { this.actors = actors; }

    public String getPlot() { return plot; }
    public void setPlot(String plot) { this.plot = plot; }

    public String getImdbRating() { return imdbRating; }
    public void setImdbRating(String imdbRating) { this.imdbRating = imdbRating; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}