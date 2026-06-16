// Song-ID aus der URL lesen (?id=1)
var songId = new URLSearchParams(window.location.search).get('id');

// Beim Laden — Song-Daten holen und Felder befüllen
window.onload = function() {
    fetch('/api/songs')
        .then(response => response.json())
        .then(songs => {
            var song = songs.find(s => s.id == songId);
            if (!song) {
                window.location.href = '/pages/songpages/songs.html';
                return;
            }
            document.getElementById('title').value       = song.title || '';
            document.getElementById('artist').value      = song.artist || '';
            document.getElementById('album').value       = song.album || '';
document.getElementById('releaseDate').value = song.releaseDate || '';
            document.getElementById('duration').value    = song.duration || '';
            document.getElementById('explicit').value    = song.explicit != null ? song.explicit : '';
            document.getElementById('coverUrl').value    = song.coverUrl || '';
            document.getElementById('spotifyUrl').value  = song.spotifyUrl || '';
        });
};

// Song speichern → PUT aufrufen
function saveSong() {
    fetch('/api/songs/' + songId, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title:       document.getElementById('title').value,
            artist:      document.getElementById('artist').value,
            album:       document.getElementById('album').value,
releaseDate: document.getElementById('releaseDate').value,
            duration:    document.getElementById('duration').value,
            explicit:    document.getElementById('explicit').value === 'true',
            coverUrl:    document.getElementById('coverUrl').value,
            spotifyUrl:  document.getElementById('spotifyUrl').value
        })
    })
    .then(response => {
        if (response.ok) {
            window.location.href = '/pages/songpages/songs.html';
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
