// Beim Laden prüfen ob eingeloggt + Filme laden
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
                loadMovies();
            }
        });
};

// Alle Filme laden
function loadMovies() {
    fetch('/api/movies')
        .then(response => response.json())
        .then(movies => {
            renderMovies(movies);
        });
}

// Filme anzeigen
function renderMovies(movies) {
    var list = document.getElementById('moviesList');

    if (movies.length === 0) {
        list.innerHTML = '<p class="empty-message">Noch keine Filme hinzugefügt.</p>';
        return;
    }

    list.innerHTML = '';
    for (var i = 0; i < movies.length; i++) {
        var movie = movies[i];
        var item = document.createElement('div');
        item.className = 'movie-item';
        item.innerHTML =
            '<img src="' + (movie.posterUrl || '/assets/no-poster.png') + '" alt="Poster">' +
            '<div class="info">' +
            '<h3>' + movie.title + '</h3>' +
            '<p>' + (movie.year || '') + ' • ' + (movie.genre || '') + '</p>' +
            '</div>' +
            '<button class="delete-btn" onclick="deleteMovie(' + movie.id + ')">Löschen</button>';
        list.appendChild(item);
    }
}

// OMDB Suche
function searchOMDB() {
    var query = document.getElementById('searchInput').value;
    var results = document.getElementById('searchResults');

    if (!query) return;

    results.innerHTML = '<p class="empty-message">Suche...</p>';

    fetch('/api/external/movies/search?q=' + encodeURIComponent(query))
        .then(response => response.json())
        .then(movies => {
            if (movies.length === 0) {
                results.innerHTML = '<p class="empty-message">Keine Ergebnisse gefunden.</p>';
                return;
            }

            results.innerHTML = '';
            for (var i = 0; i < movies.length; i++) {
                var movie = movies[i];
                var item = document.createElement('div');
                item.className = 'search-result-item';
                item.innerHTML =
                    '<img src="' + (movie.posterUrl || '/assets/no-poster.png') + '" alt="Poster">' +
                    '<div class="info">' +
                    '<h3>' + movie.title + '</h3>' +
                    '<p>' + (movie.year || '') + '</p>' +
                    '</div>' +
                    '<button onclick="addMovie(\'' + movie.title + '\', \'' + movie.year + '\', \'' + movie.genre + '\', \'' + movie.posterUrl + '\', \'' + movie.imdbId + '\')">Hinzufügen</button>';
                results.appendChild(item);
            }
        });
}

// Film hinzufügen
function addMovie(title, year, genre, posterUrl, imdbId) {
    fetch('/api/movies', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: title,
            year: year,
            genre: genre,
            posterUrl: posterUrl,
            imdbId: imdbId
        })
    })
        .then(response => {
            if (response.ok) {
                document.getElementById('searchResults').innerHTML = '';
                document.getElementById('searchInput').value = '';
                loadMovies();
            }
        });
}

// Film löschen
function deleteMovie(id) {
    fetch('/api/movies/' + id, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                loadMovies();
            }
        });
}

// Filme filtern
function filterMovies() {
    var query = document.getElementById('filterInput').value.toLowerCase();
    var items = document.querySelectorAll('.movie-item');

    for (var i = 0; i < items.length; i++) {
        var title = items[i].querySelector('h3').textContent.toLowerCase();
        if (title.includes(query)) {
            items[i].style.display = 'flex';
        } else {
            items[i].style.display = 'none';
        }
    }
}

// Abmelden
function logout() {
    fetch('/api/auth/logout', {
        method: 'POST'
    })
        .then(() => {
            window.location.href = '/pages/login.html';
        });
}