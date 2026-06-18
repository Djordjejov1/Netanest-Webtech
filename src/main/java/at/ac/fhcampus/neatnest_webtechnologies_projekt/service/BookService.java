package at.ac.fhcampus.neatnest_webtechnologies_projekt.service;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.Book;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.BookRepository;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public User getUserFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userRepository.findById(userId).orElse(null);
    }

    public List<Book> getAllBooks(HttpSession session) {
        User user = getUserFromSession(session);
        return bookRepository.findByUser(user);
    }

    public Book addBook(HttpSession session, Book book) {
        User user = getUserFromSession(session);
        if (bookRepository.existsByUserAndTitleIgnoreCase(user,book.getTitle())){
            return null;
        }
        book.setUser(user);
        return bookRepository.save(book);
    }

    public Book updateBook(HttpSession session, Long id, Book updatedBook) {
        User user = getUserFromSession(session);
        Optional<Book> existing = bookRepository.findByIdAndUser(id, user);

        if (existing.isEmpty()) return null;

        Book book = existing.get();
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setGenre(updatedBook.getGenre());
        book.setYear(updatedBook.getYear());
        book.setIsbn(updatedBook.getIsbn());
        book.setGoogleBooksUrl(updatedBook.getGoogleBooksUrl());
        book.setThumbnailUrl(updatedBook.getThumbnailUrl());
        return bookRepository.save(book);
    }

    public boolean deleteBook(HttpSession session, Long id) {
        User user = getUserFromSession(session);
        Optional<Book> existing = bookRepository.findByIdAndUser(id, user);

        if (existing.isEmpty()) return false;

        bookRepository.delete(existing.get());
        return true;
    }

    public List<Book> searchBooks(HttpSession session, String query) {
        User user = getUserFromSession(session);
        return bookRepository.findByUserAndTitleContainingIgnoreCase(user, query);
    }
}