function toggleUserMenu(event) {
    // I toggle the shared profile menu when the user clicks the menu button.
    if (event) {
        event.stopPropagation();
    }

    const menu = document.getElementById("userMenu");

    if (menu) {
        menu.classList.toggle("is-open");
    }
}

function closeUserMenu() {
    // I close the shared profile menu.
    const menu = document.getElementById("userMenu");

    if (menu) {
        menu.classList.remove("is-open");
    }
}

function openAccountInfo() {
    // I send the user to the account information page from the profile menu.
    window.location.href = "/pages/account.html";
}

function openSettings() {
    // I send the user to the settings page from the profile menu.
    window.location.href = "/pages/settings.html";
}
