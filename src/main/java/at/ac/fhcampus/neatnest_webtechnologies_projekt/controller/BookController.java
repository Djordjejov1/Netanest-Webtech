package at.ac.fhcampus.neatnest_webtechnologies_projekt.controller;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Book;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity getAllBooks(HttpSession session) {
        List<Book> books = bookService.getAllBooks(session);
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity addBook(HttpSession session, @RequestBody Book book) {
        Book saved = bookService.addBook(session, book);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBook(HttpSession session, @PathVariable Long id, @RequestBody Book book) {
        Book updated = bookService.updateBook(session, id, book);
        if (updated == null) {
            return ResponseEntity.status(404).body("Buch nicht gefunden");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBook(HttpSession session, @PathVariable Long id) {
        boolean deleted = bookService.deleteBook(session, id);
        if (!deleted) {
            return ResponseEntity.status(404).body("Buch nicht gefunden");
        }
        return ResponseEntity.ok("Buch gelöscht");
    }

    @GetMapping("/search")
    public ResponseEntity searchBooks(HttpSession session, @RequestParam String q) {
        List<Book> books = bookService.searchBooks(session, q);
        return ResponseEntity.ok(books);
    }
}