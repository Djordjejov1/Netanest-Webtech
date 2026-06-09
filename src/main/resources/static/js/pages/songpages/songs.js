var currentSong = null;
var allSongs = [];
var searchResults = [];

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
            allSongs = songs;
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
            '<button class="info-btn" onclick="openModal(' + song.id + ')">Info</button>' +
            '<button class="delete-btn" onclick="deleteSong(' + song.id + ')">Löschen</button>';
        list.appendChild(item);
    }
}

// Song Suche (Spotify API)
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

            searchResults = songs;
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
                    '<button onclick="addSong(' + i + ')">Hinzufügen</button>'
                results.appendChild(item);
            }
        });
}

// Song hinzufügen
function addSong(index) {
    var song = searchResults[index]
    fetch('/api/songs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title:      song.title,
            artist:     song.artist,
            album:      song.album,
            coverUrl:   song.coverUrl,
            spotifyUrl: song.spotifyUrl,
            explicit:    song.explicit,
            duration:    song.duration,
            releaseDate: song.releaseDate
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

function openModal(id) {
    currentSong = allSongs.find(s => s.id === id);
    document.getElementById('modalTitle').textContent = currentSong.title;
    document.getElementById('modalCover').src = currentSong.coverUrl || '';
    document.getElementById('modalInfo').innerHTML =
        '<p>Artist: ' + (currentSong.artist || 'N/A') + '</p>' +
        '<p>Album: ' + (currentSong.album || 'N/A') + '</p>' +
        '<p>Erschienen: ' + (currentSong.releaseDate || 'N/A') + '</p>' +
        '<p>Dauer: ' + (currentSong.duration || 'N/A') + '</p>' +
        '<p>Explicit: ' + (currentSong.explicit ? 'Ja 🅴' : 'Nein') + '</p>';
    document.getElementById('songModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('songModal').style.display = 'none';
}

function editSong() {
    window.location.href = '/pages/songpages/edit-song.html?id=' + currentSong.id;
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
