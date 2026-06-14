var currentBook = null;
var allBooks = [];
var searchResults = [];

window.onload = function() {
    fetch('/api/auth/me')
        .then(function(response) {
            if (!response.ok) {
                window.location.href = '/pages/login.html';
            } else {
                return response.text();
            }
        })
        .then(function(text) {
            if (text) {
                document.getElementById('welcomeText').textContent = text;
                loadBooks();
            }
        });
};

function loadBooks() {
    fetch('/api/books')
        .then(function(response) { return response.json(); })
        .then(function(books) {
            allBooks = books;
            renderBooks(books);
        });
}

function renderBooks(books) {
    var list = document.getElementById('booksList');

    if (books.length === 0) {
        list.innerHTML = '<p class="empty-message">Noch keine Bücher hinzugefügt.</p>';
        return;
    }

    list.innerHTML = '';

    for (var i = 0; i < books.length; i++) {
        var book = books[i];
        var item = document.createElement('div');
        item.className = 'movie-item';
        item.innerHTML =
            '<img src="' + (book.thumbnailUrl || '') + '" alt="Cover" onerror="this.onerror=null; this.src=\'/assets/no-cover.png\'">' +
            '<div class="info">' +
            '<h3>' + book.title + '</h3>' +
            '<p>' + (book.author || '') + ' • ' + (book.year || '') + '</p>' +
            '</div>' +
            '<button class="info-btn" onclick="openModal(' + book.id + ')">Info</button>' +
            '<button class="delete-btn" onclick="deleteBook(' + book.id + ')">Löschen</button>';
        list.appendChild(item);
    }
}

function searchBooks() {
    var query = document.getElementById('searchInput').value;
    var results = document.getElementById('searchResults');

    if (!query) return;

    results.innerHTML = '<p class="empty-message">Suche...</p>';

    fetch('/api/external/books/search?q=' + encodeURIComponent(query))
        .then(function(response) { return response.json(); })
        .then(function(books) {
            if (books.length === 0) {
                results.innerHTML = '<p class="empty-message">Keine Ergebnisse.</p>';
                return;
            }

            searchResults = books;
            results.innerHTML = '';

            for (var i = 0; i < books.length; i++) {
                var book = books[i];
                var item = document.createElement('div');
                item.className = 'search-result-item';
                item.innerHTML =
                    '<img src="' + (book.thumbnail || '') + '" alt="Cover" onerror="this.onerror=null; this.src=\'/assets/no-cover.png\'">' +
                    '<div class="info">' +
                    '<h3>' + book.title + '</h3>' +
                    '<p>' + (book.author || '') + '</p>' +
                    '</div>' +
                    '<button onclick="addBook(' + i + ')">Hinzufügen</button>';
                results.appendChild(item);
            }
        });
}

function addBook(index) {
    var book = searchResults[index];
    fetch('/api/books', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title:          book.title,
            author:         book.author,
            year:           book.year,
            isbn:           book.isbn,
            thumbnailUrl:   book.thumbnail,
            googleBooksUrl: ''
        })
    })
        .then(function(response) {
            if (response.ok) {
                document.getElementById('searchResults').innerHTML = '';
                document.getElementById('searchInput').value = '';
                loadBooks();
            }
        });
}

function deleteBook(id) {
    fetch('/api/books/' + id, { method: 'DELETE' })
        .then(function(response) {
            if (response.ok) loadBooks();
        });
}

function applySortFilter() {
    var sortValue = document.getElementById('sortSelect').value;
    var sorted = allBooks.slice();

    if (sortValue === 'alpha-asc') {
        sorted.sort(function(a, b) { return a.title.localeCompare(b.title); });

    } else if (sortValue === 'alpha-desc') {
        sorted.sort(function(a, b) { return b.title.localeCompare(a.title); });

    } else if (sortValue === 'author-asc') {
        sorted.sort(function(a, b) { return (a.author || '').localeCompare(b.author || ''); });

    } else if (sortValue === 'author-desc') {
        sorted.sort(function(a, b) { return (b.author || '').localeCompare(a.author || ''); });

    } else if (sortValue === 'year-asc') {
        sorted.sort(function(a, b) { return parseInt(a.year || 0) - parseInt(b.year || 0); });

    } else if (sortValue === 'year-desc') {
        sorted.sort(function(a, b) { return parseInt(b.year || 0) - parseInt(a.year || 0); });
    }

    renderBooks(sorted);
}

function openModal(id) {
    for (var i = 0; i < allBooks.length; i++) {
        if (allBooks[i].id === id) {
            currentBook = allBooks[i];
            break;
        }
    }

    document.getElementById('modalTitle').textContent = currentBook.title;
    document.getElementById('modalCover').src = currentBook.thumbnailUrl || '';
    document.getElementById('modalCover').onerror = function() {
        this.onerror = null;
        this.src = '/assets/no-cover.png';
    };
    document.getElementById('modalInfo').innerHTML =
        '<p>Autor: ' + (currentBook.author || 'N/A') + '</p>' +
        '<p>Jahr: '  + (currentBook.year   || 'N/A') + '</p>' +
        '<p>Genre: ' + (currentBook.genre  || 'N/A') + '</p>' +
        '<p>ISBN: '  + (currentBook.isbn   || 'N/A') + '</p>';

    document.getElementById('bookModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('bookModal').style.display = 'none';
}

function editBook() {
    window.location.href = '/pages/bookpages/edit-book.html?id=' + currentBook.id;
}

// Buch zu Favoriten hinzufügen
function addToFavorites() {
    fetch('/api/favorites', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            mediaType: 'BOOK',
            mediaId:   currentBook.id,
            title:     currentBook.title,
            imageUrl:  currentBook.thumbnailUrl || ''
        })
    })//
        .then(function(response) {
            if (response.ok) {
                alert('Zu Favoriten hinzugefügt!');
                closeModal();
            } else if (response.status === 409) {
                alert('Bereits in deinen Favoriten!');
                closeModal();
            } else {
                alert('Fehler beim Hinzufügen.');
            }
        });
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(function() {
            window.location.href = '/pages/login.html';
        });
}