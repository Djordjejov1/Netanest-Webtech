// Beim Laden prüfen ob User bereits eingeloggt ist
window.onload = function() {
    fetch('/api/auth/me')
        .then(response => {
            if (response.ok) {
                // Bereits eingeloggt → weiterleiten
                window.location.href = '/pages/movies.html';
            }
        });
};

// Login
function login() {
    var username = document.getElementById('loginUsername').value;
    var password = document.getElementById('loginPassword').value;
    var message = document.getElementById('loginMessage');

    fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username, password: password })
    })
        .then(response => {
            if (response.ok) {
                message.className = 'message success';
                message.textContent = 'Login erfolgreich!';
                window.location.href = '/pages/movies.html';
            } else {
                message.className = 'message error';
                message.textContent = 'Falscher Username oder Passwort!';
            }
        });
}

// Register
function register() {
    var username = document.getElementById('registerUsername').value;
    var password = document.getElementById('registerPassword').value;
    var message = document.getElementById('registerMessage');

    fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username, password: password })
    })
        .then(response => {
            if (response.ok) {
                message.className = 'message success';
                message.textContent = 'Registrierung erfolgreich! Bitte einloggen.';
                showLogin();
            } else {
                message.className = 'message error';
                message.textContent = 'Username bereits vergeben!';
            }
        });
}

// Zwischen Login und Register wechseln
function showRegister() {
    document.getElementById('loginCard').style.display = 'none';
    document.getElementById('registerCard').style.display = 'block';
}

function showLogin() {
    document.getElementById('registerCard').style.display = 'none';
    document.getElementById('loginCard').style.display = 'block';
}