package at.ac.fhcampus.neatnest_webtechnologies_projekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //-> für favoritepage
public class NeatnestWebtechnologiesProjektApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeatnestWebtechnologiesProjektApplication.class, args);
    }

}
