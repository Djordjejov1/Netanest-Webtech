var bookId = new URLSearchParams(window.location.search).get('id');
var currentBook = null; // <-- altes Buch merken!

window.onload = function() {
    fetch('/api/books')
        .then(function(response) { return response.json(); })
        .then(function(books) {
            for (var i = 0; i < books.length; i++) {
                if (books[i].id == bookId) {
                    currentBook = books[i]; // <-- komplettes Objekt speichern
                    break;
                }
            }
            if (!currentBook) {
                window.location.href = '/pages/bookpages/books.html';
                return;
            }
            document.getElementById('title').value  = currentBook.title  || '';
            document.getElementById('author').value = currentBook.author || '';
            document.getElementById('year').value   = currentBook.year   || '';
            document.getElementById('genre').value  = currentBook.genre  || '';
            document.getElementById('isbn').value   = currentBook.isbn   || '';
        });
};

function saveBook() {
    fetch('/api/books/' + bookId, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title:         document.getElementById('title').value,
            author:        document.getElementById('author').value,
            year:          document.getElementById('year').value,
            genre:         document.getElementById('genre').value,
            isbn:          document.getElementById('isbn').value,
            // ↓ alte Werte behalten, nicht löschen!
            thumbnailUrl:  currentBook ? currentBook.thumbnailUrl  : '',
            googleBooksUrl: currentBook ? currentBook.googleBooksUrl : ''
        })
    })
        .then(function(response) {
            if (response.ok) {
                window.location.href = '/pages/bookpages/books.html';
            } else {
                alert('Fehler beim Speichern!');
            }
        });
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(function() {
            window.location.href = '/pages/login.html';
        });
}