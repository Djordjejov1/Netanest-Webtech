var currentMovie = null;
var allMovies = [];
var searchResults = [];

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
            allMovies = movies;
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
            '<button class="info-btn" onclick="openModal(' + movie.id + ')">Info</button>' +
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

            searchResults = movies; // alle Suchergebnisse speichern
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
                    '<button onclick="addMovie(' + i + ')">Hinzufügen</button>';
                results.appendChild(item);
            }
        });
}

// Film hinzufügen — Index aus searchResults Array
function addMovie(index) {
    var movie = searchResults[index];
    fetch('/api/movies', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title:      movie.title,
            year:       movie.year,
            genre:      movie.genre,
            director:   movie.director,
            posterUrl:  movie.posterUrl,
            imdbId:     movie.imdbId,
            released:   movie.released,
            runtime:    movie.runtime,
            actors:     movie.actors,
            plot:       movie.plot,
            imdbRating: movie.imdbRating
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

function openModal(id) {
    currentMovie = allMovies.find(m => m.id === id);
    document.getElementById('modalTitle').textContent = currentMovie.title;
    document.getElementById('modalPoster').src = currentMovie.posterUrl || '';
    document.getElementById('modalPoster').style.display = currentMovie.posterUrl ? 'block' : 'none';
    document.getElementById('modalInfo').innerHTML =
        '<p>IMDb: ' + (currentMovie.imdbRating || 'N/A') + '</p>' +
        '<p>Erschienen: ' + (currentMovie.released || currentMovie.year || 'N/A') + '</p>' +
        '<p>Laufzeit: ' + (currentMovie.runtime || 'N/A') + '</p>' +
        '<p>Genre: ' + (currentMovie.genre || 'N/A') + '</p>' +
        '<p>Regisseur: ' + (currentMovie.director || 'N/A') + '</p>' +
        '<p>Schauspieler: ' + (currentMovie.actors || 'N/A') + '</p>' +
        '<p>Plot: ' + (currentMovie.plot || 'N/A') + '</p>';
    document.getElementById('movieModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('movieModal').style.display = 'none';
}

function editMovie() {
    // zur Edit-Page mit der Film-ID
    window.location.href = '/pages/moviespages/edit-movie.html?id=' + currentMovie.id;
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

//Favorite - Funktion
function addToFavorites() {
    fetch('/api/favorites', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            mediaType: 'MOVIE',
            mediaId: currentMovie.id,
            title: currentMovie.title,
            imageUrl: currentMovie.posterUrl || ''
        })
    })
        .then(response => {
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