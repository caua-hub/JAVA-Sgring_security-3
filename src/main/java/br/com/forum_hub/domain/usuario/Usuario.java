package br.com.forum_hub.domain.usuario;

import br.com.forum_hub.domain.perfil.Perfil;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;

    private String nomeUsuario;

    private String email;

    private String senha;

    private String biografia;

    private String miniBiografia;

    private Boolean verificado;
    private String token;
    private LocalDateTime expiracaoToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_perfis",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id"))
    private List<Perfil> perfis = new ArrayList<Perfil>();

    public Usuario() {
    }

    public Usuario(@Valid DadosCadastroUsuario dados, String senha, Perfil perfis) {
        this.nomeCompleto = dados.nomeCompleto();
        this.email = dados.email();
        this.senha = senha;
        this.nomeUsuario = dados.nomeUsuario();
        this.biografia = dados.biografia();
        this.miniBiografia = dados.miniBiografia();
        this.verificado = false;
        this.token = UUID.randomUUID().toString();
        this.perfis.add(perfis);
        this.expiracaoToken = LocalDateTime.now().plusMinutes(30);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return perfis;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getMiniBiografia() {
        return miniBiografia;
    }

    public String getBiografia() {
        return biografia;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public Long getId() {
        return this.id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getToken() {
        return token;
    }

    public void verificar() {
        if(this.expiracaoToken.isBefore(LocalDateTime.now())) {
            throw new RegraDeNegocioException("Tempo para verificacao de usuario expirado.");
        }
        this.verificado = true;
        this.token = null;
        this.expiracaoToken = null;
    }

    public void adicionarPerfil(Perfil perfil) {
        this.perfis.add(perfil);
    }
}
