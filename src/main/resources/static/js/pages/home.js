var allFavorites = [];

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
                document.getElementById('welcomeText').textContent = 'Willkommen ' + text.replace('Eingeloggt als', '').replace(':', '').trim();
                loadFavorites();
            }
        });
};

function loadFavorites() {
    fetch('/api/favorites')
        .then(response => response.json())
        .then(favorites => {
            allFavorites = favorites;
            renderFavorites(favorites);
            setInterval(updateCountdowns, 1000);
        });
}

function filterFavorites() {
    var query = document.querySelector('.favorites-header input').value.toLowerCase();
    var filtered = allFavorites.filter(function(fav) {
        return fav.title.toLowerCase().includes(query);
    });
    renderFavorites(filtered);
}

function renderFavorites(favorites) {
    var list = document.getElementById('favoritesList');
    if (!list) return;

    if (favorites.length === 0) {
        list.innerHTML =
            '<div class="favorite-item"><span>🎬</span>' +
            '<div><h3>Noch keine Favoriten</h3>' +
            '<p>Füge später Filme, Songs oder Bücher hinzu.</p></div></div>';
        return;
    }

    list.innerHTML = '';
    for (var i = 0; i < favorites.length; i++) {
        var fav = favorites[i];
        var item = document.createElement('div');
        item.className = 'favorite-item';
        item.id = 'fav-' + fav.id;
        item.innerHTML =
            '<img src="' + (fav.imageUrl || '') + '" alt="Cover" onerror="this.onerror=null;this.src=\'/assets/no-cover.png\'">' +
            '<div class="info">' +
            '<h3>' + fav.title + '</h3>' +
            '<p>' + fav.mediaType + '</p>' +
            '<p class="countdown" id="countdown-' + fav.id + '">Lädt...</p>' +
            '</div>' +
            '<button class="fav-remove-btn" onclick="deleteFavorite(' + fav.id + ')">⭐</button>';
        list.appendChild(item);
    }
}

function updateCountdowns() {
    var now = new Date();
    for (var i = 0; i < allFavorites.length; i++) {
        var fav = allFavorites[i];
        var el = document.getElementById('countdown-' + fav.id);
        if (!el) continue;

        var diff = new Date(fav.expiresAt) - now;
        if (diff <= 0) {
            var card = document.getElementById('fav-' + fav.id);
            if (card) card.remove();
            continue;
        }

        var hours   = Math.floor(diff / 3600000);
        var minutes = Math.floor((diff % 3600000) / 60000);
        var seconds = Math.floor((diff % 60000) / 1000);
        el.textContent = 'Läuft ab in: ' + hours + 'h ' + minutes + 'm ' + seconds + 's';
        if (hours < 1) el.classList.add('urgent');
    }
}

function deleteFavorite(id) {
    fetch('/api/favorites/' + id, { method: 'DELETE' })
        .then(response => {
            if (response.ok) {
                var card = document.getElementById('fav-' + id);
                if (card) card.remove();
                allFavorites = allFavorites.filter(f => f.id !== id);
                if (allFavorites.length === 0) renderFavorites([]);
            }
        });
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => {
            window.location.href = '/pages/login.html';
        });
}

function toggleUserMenu() {
    var menu = document.getElementById('userMenu');
    menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
}

function openAccountInfo() {
    window.location.href = '/pages/account.html';
}