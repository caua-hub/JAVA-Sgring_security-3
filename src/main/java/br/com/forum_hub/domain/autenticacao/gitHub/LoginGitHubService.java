package br.com.forum_hub.domain.autenticacao.gitHub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class LoginGitHubService {

    private final String clientId = "Ov23li1IbxSj14V3pwUS";
    private final String clientSecret = "61680ee797eb5fcdf2c010ff7e13364762cd80af";
    private final String redirectUri = "http://localhost:8080/login/github/autorizada";

    @Autowired
    private RestClient.Builder restClient;

    public String geraUrl() {
        return "https://github.com/login/oauth/authorize"+
                "?client_id="+clientId +
                "&redirect_uri="+redirectUri +
                "&scope=read:user,user:email";
    }

    private String obterToken(String code) {
        var resposta = restClient.build()
                .post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("code", code, "client_id", clientId, "client_secret", clientSecret,
                        "redirect_uri", redirectUri))
                .retrieve()
                .body(Map.class);
        return resposta.get("access_token").toString();
    }
    public String obterEmailToken(String code) {
        var token = obterToken(code);
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var resposta = restClient.build()
                .get()
                .uri("https://api.github.com/user/emails")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DadosEmail[].class);

        for(DadosEmail dados : resposta){
            if (dados.primary() && dados.verified())
                return dados.email();
        }
        return null;
    }
}
