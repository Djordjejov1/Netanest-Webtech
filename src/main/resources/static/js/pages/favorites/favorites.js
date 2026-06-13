var allFavorites = [];

// Beim Laden prüfen ob eingeloggt + Favoriten laden
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
                loadFavorites();
            }
        });
};

// Alle Favoriten vom Backend laden
function loadFavorites() {
    fetch('/api/favorites')
        .then(function(response) { return response.json(); })
        .then(function(favorites) {
            allFavorites = favorites;
            renderFavorites(favorites);
            // Countdown jede Sekunde aktualisieren
            setInterval(updateCountdowns, 1000);
        });
}

// Favoriten anzeigen
function renderFavorites(favorites) {
    var list = document.getElementById('favoritesList');

    if (favorites.length === 0) {
        list.innerHTML = '<p class="empty-message">Noch keine Favoriten vorhanden.</p>';
        return;
    }

    list.innerHTML = '';

    for (var i = 0; i < favorites.length; i++) {
        var fav = favorites[i];

        var item = document.createElement('div');
        item.className = 'favorite-card';
        item.id = 'fav-' + fav.id;

        item.innerHTML =
            '<img src="' + (fav.imageUrl || '') + '" alt="Cover" onerror="this.onerror=null; this.src=\'/assets/no-cover.png\'">' +
            '<div class="info">' +
            '<p class="media-type">' + fav.mediaType + '</p>' +
            '<h3>' + fav.title + '</h3>' +
            '<p class="countdown" id="countdown-' + fav.id + '">Lädt...</p>' +
            '</div>' +
            '<button class="delete-btn" onclick="deleteFavorite(' + fav.id + ')">Löschen</button>';

        list.appendChild(item);
    }
}

// Countdown für alle Favoriten aktualisieren
function updateCountdowns() {
    var now = new Date();

    for (var i = 0; i < allFavorites.length; i++) {
        var fav = allFavorites[i];
        var countdownEl = document.getElementById('countdown-' + fav.id);

        if (!countdownEl) continue;

        // expiresAt vom Server kommt als String → in Date umwandeln
        var expiresAt = new Date(fav.expiresAt);

        // Wie viele Millisekunden noch übrig?
        var diff = expiresAt - now;

        if (diff <= 0) {
            // Abgelaufen → Karte aus der Ansicht entfernen
            var card = document.getElementById('fav-' + fav.id);
            if (card) card.remove();
            continue;
        }

        // Stunden, Minuten, Sekunden berechnen
        var hours   = Math.floor(diff / 3600000);
        var minutes = Math.floor((diff % 3600000) / 60000);
        var seconds = Math.floor((diff % 60000) / 1000);

        var text = 'Läuft ab in: ' + hours + 'h ' + minutes + 'm ' + seconds + 's';
        countdownEl.textContent = text;

        // Unter 1 Stunde → rot färben
        if (hours < 1) {
            countdownEl.classList.add('urgent');
        }
    }
}

// Favoriten manuell löschen
function deleteFavorite(id) {
    fetch('/api/favorites/' + id, { method: 'DELETE' })
        .then(function(response) {
            if (response.ok) {
                // Karte direkt aus dem DOM entfernen
                var card = document.getElementById('fav-' + id);
                if (card) card.remove();

                // Aus dem Array entfernen
                allFavorites = allFavorites.filter(function(f) {
                    return f.id !== id;
                });

                // Falls Liste leer ist
                if (allFavorites.length === 0) {
                    document.getElementById('favoritesList').innerHTML =
                        '<p class="empty-message">Noch keine Favoriten vorhanden.</p>';
                }
            }
        });
}

// Abmelden
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(function() {
            window.location.href = '/pages/login.html';
        });
}