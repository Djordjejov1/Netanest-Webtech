window.onload = function() {
    fetch('/api/auth/me')
        .then(response => {
            if (!response.ok) {
                window.location.href = '/pages/login.html';
            } else {
                return response.text();
            }
        })
        .then(text => {
            if (text) {
                document.getElementById('welcomeText').textContent = text;
                loadBooks();
            }
        });
};

function loadBooks() {
    fetch('/api/books')
        .then(response => response.json())
        .then(books => renderBooks(books));
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
            '<img src="' + (book.thumbnailUrl || '/assets/no-cover.png') + '" alt="Cover">' +
            '<div class="info">' +
            '<h3>' + book.title + '</h3>' +
            '<p>' + (book.author || '') + ' • ' + (book.year || '') + '</p>' +
            '</div>' +
            '<button class="delete-btn" onclick="deleteBook(' + book.id + ')">Löschen</button>';
        list.appendChild(item);
    }
}

function searchGoogleBooks() {
    var query = document.getElementById('searchInput').value;
    var results = document.getElementById('searchResults');

    if (!query) return;

    results.innerHTML = '<p class="empty-message">Suche...</p>';

    fetch('/api/external/books/search?q=' + encodeURIComponent(query))
        .then(response => response.json())
        .then(books => {
            if (books.length === 0) {
                results.innerHTML = '<p class="empty-message">Keine Ergebnisse.</p>';
                return;
            }

            results.innerHTML = '';
            for (var i = 0; i < books.length; i++) {
                var book = books[i];
                var item = document.createElement('div');
                item.className = 'search-result-item';
                item.innerHTML =
                    '<img src="' + (book.thumbnail || '/assets/no-cover.png') + '" alt="Cover">' +
                    '<div class="info">' +
                    '<h3>' + book.title + '</h3>' +
                    '<p>' + (book.author || '') + '</p>' +
                    '</div>' +
                    '<button onclick="addBook(\'' + book.title + '\', \'' + book.author + '\', \'' + book.year + '\', \'' + book.isbn + '\', \'' + book.thumbnail + '\', \'' + book.googleBooksUrl + '\')">Hinzufügen</button>';
                results.appendChild(item);
            }
        });
}

function addBook(title, author, year, isbn, thumbnailUrl, googleBooksUrl) {
    fetch('/api/books', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: title,
            author: author,
            year: year,
            isbn: isbn,
            thumbnailUrl: thumbnailUrl,
            googleBooksUrl: googleBooksUrl
        })
    })
        .then(response => {
            if (response.ok) {
                document.getElementById('searchResults').innerHTML = '';
                document.getElementById('searchInput').value = '';
                loadBooks();
            }
        });
}

function deleteBook(id) {
    fetch('/api/books/' + id, { method: 'DELETE' })
        .then(response => {
            if (response.ok) loadBooks();
        });
}

function filterBooks() {
    var query = document.getElementById('filterInput').value.toLowerCase();
    var items = document.querySelectorAll('.movie-item');

    for (var i = 0; i < items.length; i++) {
        var title = items[i].querySelector('h3').textContent.toLowerCase();
        items[i].style.display = title.includes(query) ? 'flex' : 'none';
    }
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => {
            window.location.href = '/pages/login.html';
        });
}