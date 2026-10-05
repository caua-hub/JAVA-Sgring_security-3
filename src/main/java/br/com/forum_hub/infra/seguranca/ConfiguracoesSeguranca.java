package br.com.forum_hub.infra.seguranca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracoesSeguranca {

    @Autowired
    FiltroTokenAcesso filtroTokenAcesso;

    @Bean
    public SecurityFilterChain confuguracaoFiltrosSeguranca(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeRequests -> {
                            authorizeRequests.requestMatchers("/login/**", "/atualizar-token", "/registrar", "/verificar-conta", "/{nomeUsuario}").permitAll();
                            authorizeRequests.requestMatchers(HttpMethod.GET, "/cursos").permitAll();
                            authorizeRequests.requestMatchers(HttpMethod.GET, "/topicos/**").permitAll();

                            authorizeRequests.requestMatchers(HttpMethod.PUT, "/topicos").hasRole("ESTUDANTE");
                            authorizeRequests.requestMatchers(HttpMethod.POST, "/topicos").hasRole("ESTUDANTE");
                            authorizeRequests.requestMatchers(HttpMethod.DELETE, "/topicos/**").hasRole("ESTUDANTE");

                            authorizeRequests.requestMatchers(HttpMethod.PATCH, "/topicos/{idTopico}/respostas/**").hasAnyRole("INSTRUTOR", "ESTUDANTE");
                            authorizeRequests.requestMatchers(HttpMethod.PATCH, "/topicos/**").hasRole("MODERADOR");

                            authorizeRequests.requestMatchers(HttpMethod.PATCH, "/adicionar-perfil/**").hasRole("ADIMIN");


                    authorizeRequests.anyRequest().authenticated();
                        }
                )
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(cs -> cs.disable())
                .addFilterBefore(filtroTokenAcesso, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();

    }

    @Bean
    public RoleHierarchy hierarquiaDePerfil() {
        String hierarquia = """
            ROLE_ADMIN > ROLE_MODERADOR
            ROLE_MODERADOR > ROLE_ESTUDANTE
            ROLE_MODERADOR > ROLE_INSTRUTOR
            """;
        return RoleHierarchyImpl.fromHierarchy(hierarquia);
    }

}
