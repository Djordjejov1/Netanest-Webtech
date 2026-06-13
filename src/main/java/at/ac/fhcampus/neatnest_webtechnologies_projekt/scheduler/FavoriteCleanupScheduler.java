package at.ac.fhcampus.neatnest_webtechnologies_projekt.scheduler;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FavoriteCleanupScheduler {

    @Autowired
    private FavoriteService favoriteService;

    // Läuft alle 60 Sekunden automatisch
    // 60000 Millisekunden = 60 Sekunden
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredFavorites() {
        favoriteService.deleteExpiredFavorites();
    }
}