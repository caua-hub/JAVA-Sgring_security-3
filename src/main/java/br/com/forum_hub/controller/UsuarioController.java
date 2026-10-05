package br.com.forum_hub.controller;

import br.com.forum_hub.domain.perfil.DadosPerfil;
import br.com.forum_hub.domain.topico.DadosCadastroTopico;
import br.com.forum_hub.domain.topico.DadosListagemTopico;
import br.com.forum_hub.domain.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class UsuarioController {

    @Autowired
    UsuarioService service;

    @Autowired
    UsuarioRepository usuarioRepository;

    @PostMapping("/registrar")
    public ResponseEntity<DadosListagemUsuario> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder){
        Usuario usuario = service.cadastrar(dados);
        var uri = uriBuilder.path("/{nomeUsuario}").buildAndExpand(usuario.getNomeUsuario()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemUsuario(usuario));
    }

    @GetMapping("/verificar-conta")
    public ResponseEntity<String>  verificarConta(@RequestParam String codigo){
        service.verificarEmail(codigo);
        return ResponseEntity.ok("Conta verificada com sucesso!!");
    }

    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<DadosListagemUsuario> detalhesUsuario(@PathVariable String nomeUsuario){
        Usuario user = usuarioRepository.findByNomeUsuarioIgnoreCase(nomeUsuario).orElseThrow();
        return ResponseEntity.ok(new DadosListagemUsuario(user));
    }

    @PutMapping("/editar-perfil")
    public ResponseEntity editaPerfil(@RequestBody DadosEditarPerfil dados, @AuthenticationPrincipal Usuario logado){
         service.editarPerfil(dados, logado);
        return ResponseEntity.ok(new DadosListagemUsuario(logado));
    }

    // código omitido

    @PatchMapping("adicionar-perfil/{id}")
    public ResponseEntity<DadosListagemUsuario> adicionarPerfil(@PathVariable Long id, @RequestBody @Valid DadosPerfil dados){
        var usuario = service.editarPerfilEnum(id, dados);
        return ResponseEntity.ok(new DadosListagemUsuario(usuario));
    }
}


