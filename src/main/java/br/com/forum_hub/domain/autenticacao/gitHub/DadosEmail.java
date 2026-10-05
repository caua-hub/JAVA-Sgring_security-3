package br.com.forum_hub.domain.autenticacao.gitHub;

public record DadosEmail(String email,
                         Boolean primary,
                         Boolean verified,
                         String visibility) {
}
