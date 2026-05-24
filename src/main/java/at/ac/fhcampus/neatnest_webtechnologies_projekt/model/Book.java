package at.ac.fhcampus.neatnest_webtechnologies_projekt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;
    private String genre;

    @Column(name = "release_year")
    private String year;

    private String isbn;
    private String googleBooksUrl;
    private String thumbnailUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getGoogleBooksUrl() { return googleBooksUrl; }
    public void setGoogleBooksUrl(String googleBooksUrl) { this.googleBooksUrl = googleBooksUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}