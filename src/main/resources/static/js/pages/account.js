window.onload = function() {
    fetch('/api/auth/account')
        .then(function(response) {
            if (!response.ok) {
                window.location.href = '/pages/login.html';
                return;
            }
            return response.json();
        })
        .then(function(data) {
            if (!data) return;
            document.getElementById('accountId').value = data.id;
            document.getElementById('username').value = data.username;
        });
};

function saveUsername() {
    var newUsername = document.getElementById('username').value;

    fetch('/api/auth/account/username', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: newUsername })
    })
        .then(function(response) { return response.text(); })
        .then(function(text) { alert(text); });
}

function savePassword() {
    var currentPassword = document.getElementById('currentPassword').value;
    var newPassword = document.getElementById('newPassword').value;
    var confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
        alert('Neue Passwörter stimmen nicht überein!');
        return;
    }

    fetch('/api/auth/account/password', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ currentPassword: currentPassword, newPassword: newPassword })
    })
        .then(function(response) { return response.text(); })
        .then(function(text) { alert(text); });
}