// Beim Laden prüfen ob eingeloggt + Songs laden
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
                loadSongs();
            }
        });
};

// Alle Songs laden
function loadSongs() {
    fetch('/api/songs')
        .then(response => response.json())
        .then(songs => {
            renderSongs(songs);
        });
}

// Songs anzeigen
function renderSongs(songs) {
    var list = document.getElementById('songsList');

    if (songs.length === 0) {
        list.innerHTML = '<p class="empty-message">Noch keine Songs hinzugefügt.</p>';
        return;
    }

    list.innerHTML = '';
    for (var i = 0; i < songs.length; i++) {
        var song = songs[i];
        var item = document.createElement('div');
        item.className = 'song-item';
        item.innerHTML =
            '<img src="' + (song.coverUrl || '/assets/no-cover.png') + '" alt="Cover">' +
            '<div class="info">' +
            '<h3>' + song.title + '</h3>' +
            '<p>' + (song.artist || '') + ' • ' + (song.album || '') + '</p>' +
            '</div>' +
            '<button class="delete-btn" onclick="deleteSong(' + song.id + ')">Löschen</button>';
        list.appendChild(item);
    }
}

// Song Suche (Spotify API - kommt noch)
function searchSongs() {
    var query = document.getElementById('searchInput').value;
    var results = document.getElementById('searchResults');

    if (!query) return;

    results.innerHTML = '<p class="empty-message">Suche...</p>';

    fetch('/api/external/songs/search?q=' + encodeURIComponent(query))
        .then(response => response.json())
        .then(songs => {
            if (songs.length === 0) {
                results.innerHTML = '<p class="empty-message">Keine Ergebnisse gefunden.</p>';
                return;
            }

            results.innerHTML = '';
            for (var i = 0; i < songs.length; i++) {
                var song = songs[i];
                var item = document.createElement('div');
                item.className = 'search-result-item';
                item.innerHTML =
                    '<img src="' + (song.coverUrl || '/assets/no-cover.png') + '" alt="Cover">' +
                    '<div class="info">' +
                    '<h3>' + song.title + '</h3>' +
                    '<p>' + (song.artist || '') + ' • ' + (song.album || '') + '</p>' +
                    '</div>' +
                    '<button onclick="addSong(\'' + song.title + '\', \'' + song.artist + '\', \'' + song.album + '\', \'' + song.coverUrl + '\', \'' + song.spotifyUrl + '\')">Hinzufügen</button>';
                results.appendChild(item);
            }
        });
}

// Song hinzufügen
function addSong(title, artist, album, coverUrl, spotifyUrl) {
    fetch('/api/songs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: title,
            artist: artist,
            album: album,
            coverUrl: coverUrl,
            spotifyUrl: spotifyUrl
        })
    })
        .then(response => {
            if (response.ok) {
                document.getElementById('searchResults').innerHTML = '';
                document.getElementById('searchInput').value = '';
                loadSongs();
            }
        });
}

// Song löschen
function deleteSong(id) {
    fetch('/api/songs/' + id, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                loadSongs();
            }
        });
}

// Songs filtern
function filterSongs() {
    var query = document.getElementById('filterInput').value.toLowerCase();
    var items = document.querySelectorAll('.song-item');

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
