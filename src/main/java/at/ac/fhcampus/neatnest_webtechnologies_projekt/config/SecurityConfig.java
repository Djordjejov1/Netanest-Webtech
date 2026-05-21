package at.ac.fhcampus.neatnest_webtechnologies_projekt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration // sagen damit, dass diese Klasse Configurationen hat
public class SecurityConfig {

    @Bean // diese Methode gibt dir ein Objekt zurück was du verwalten sollst
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Diese Endpoints sind ohne Login erreichbar
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/pages/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // Alles andere braucht Login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //definiert wie passwörter verschlüssel werden. Wird abgerufen beim speichern in die DB
    }
}