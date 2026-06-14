package at.ac.fhcampus.neatnest_webtechnologies_projekt.config;

import at.ac.fhcampus.neatnest_webtechnologies_projekt.model.User;
import at.ac.fhcampus.neatnest_webtechnologies_projekt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // sagen damit, dass diese Klasse Configurationen hat
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    // Spring muss wissen wie es einen User aus der DB laden soll
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User nicht gefunden"));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };


        //Wir erstellen einen Bean, spring ruft automatisch diese Methode auf , wenn sich jemand einloggt
        //danach haben wir ein Lambda. Springt gibt uns den eingegeben Username vom Login Formular. Wir sagen mit Spring somit
        //wenn sich jemand einlogt mach bitte das folgende.
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean // diese Methode gibt dir ein Objekt zurück was du verwalten sollst
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 Console braucht iFrames
                .securityContext(context -> context.requireExplicitSave(false)) // Session wird automatisch gespeichert
                .authorizeHttpRequests(auth -> auth
                                // Diese Endpoints sind ohne Login erreichbar
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/pages/**").permitAll()
                                .requestMatchers("/css/**").permitAll()
                                .requestMatchers("/js/**").permitAll()
                                .requestMatchers("/assets/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/", "/error", "/index.html").permitAll() // Root und Fehlerseite freigeben

                                // Alles andere braucht Login
                                .anyRequest().authenticated()
                                //-> genau da kommt die Sicherheit her bei den Controllerklassen :D
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //definiert wie passwörter codiert werden. Wird abgerufen beim speichern in die DB
    }
}