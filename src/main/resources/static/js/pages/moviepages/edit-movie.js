// Film-ID aus der URL lesen (?id=1)
var movieId = new URLSearchParams(window.location.search).get('id');

// Beim Laden — Film-Daten holen und Felder befüllen
window.onload = function() {
    fetch('/api/movies')
        .then(response => response.json())
        .then(movies => {
            // Film mit passender ID finden
            var movie = movies.find(m => m.id == movieId);
            if (!movie) {
                window.location.href = '/pages/moviespages/movies.html';
                return;
            }
            // Felder befüllen
            document.getElementById('title').value      = movie.title || '';
            document.getElementById('year').value       = movie.year || '';
            document.getElementById('genre').value      = movie.genre || '';
            document.getElementById('director').value   = movie.director || '';
            document.getElementById('released').value   = movie.released || '';
            document.getElementById('runtime').value    = movie.runtime || '';
            document.getElementById('actors').value     = movie.actors || '';
            document.getElementById('imdbRating').value = movie.imdbRating || '';
            document.getElementById('plot').value       = movie.plot || '';
            document.getElementById('posterUrl').value  = movie.posterUrl || '';
            document.getElementById('imdbId').value     = movie.imdbId || '';
        });
};

// Film speichern → PUT aufrufen
function saveMovie() {
    fetch('/api/movies/' + movieId, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title:      document.getElementById('title').value,
            year:       document.getElementById('year').value,
            genre:      document.getElementById('genre').value,
            director:   document.getElementById('director').value,
            released:   document.getElementById('released').value,
            runtime:    document.getElementById('runtime').value,
            actors:     document.getElementById('actors').value,
            imdbRating: document.getElementById('imdbRating').value,
            plot:       document.getElementById('plot').value,
            posterUrl:  document.getElementById('posterUrl').value,
            imdbId:     document.getElementById('imdbId').value
        })
    })
    .then(response => {
        if (response.ok) {
            // zurück zur Movies-Page
            window.location.href = '/pages/moviespages/movies.html';
        }
    });
}

// Abmelden
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => {
            window.location.href = '/pages/login.html';
        });
}