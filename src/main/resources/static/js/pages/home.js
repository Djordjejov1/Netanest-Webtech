// Beim Laden prüfen ob eingeloggt
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
            }
        });
};

// Abmelden
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => {
            window.location.href = '/pages/login.html';
        });
}