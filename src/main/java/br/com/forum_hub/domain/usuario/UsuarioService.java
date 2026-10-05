package br.com.forum_hub.domain.usuario;

import br.com.forum_hub.domain.perfil.DadosPerfil;
import br.com.forum_hub.domain.perfil.Perfil;
import br.com.forum_hub.domain.perfil.PerfilNome;
import br.com.forum_hub.domain.perfil.PerfilRepository;
import br.com.forum_hub.infra.email.EmailService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PerfilRepository perfilRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado!"));
    }

    @Transactional
    public Usuario cadastrar(@Valid DadosCadastroUsuario dados) {
        String senha = passwordEncoder.encode(dados.senha());
        Perfil perfil = perfilRepository.findByNome(PerfilNome.ESTUDANTE);
        Usuario usuario = new Usuario(dados, senha, perfil);
        emailService.enviarEmailVerificacao(usuario);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void verificarEmail(String codigo) {
        Usuario user = usuarioRepository.findByToken(codigo).orElseThrow();
        user.verificar();
    }

    @Transactional
    public void editarPerfil(DadosEditarPerfil dados, Usuario logado) {
        usuarioRepository.atualizar(dados.nomeCompleto(), dados.biografia(), dados.miniBiografia(), logado.getId());
    }

    @Transactional
    public Usuario editarPerfilEnum(Long id, DadosPerfil dados) {
        var user = usuarioRepository.findById(id).orElseThrow();
        var perfil = perfilRepository.findByNome(dados.perfilNome());
        user.adicionarPerfil(perfil);
        return user;
    }

}
