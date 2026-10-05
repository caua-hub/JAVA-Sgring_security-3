package br.com.forum_hub.controller;

import br.com.forum_hub.domain.autenticacao.gitHub.LoginGitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/login/github")
public class LoginGitHubController {

    @Autowired
    private LoginGitHubService loginGHService;

    @GetMapping
    public ResponseEntity<Void> redirecionarGithub() {
        var headers = new HttpHeaders();
        String url = loginGHService.geraUrl();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/autorizada")
    public ResponseEntity<String> obtemToken(@RequestParam String code) {
        var token = loginGHService.obterEmailToken(code);
        return ResponseEntity.ok(token);
    }
}
