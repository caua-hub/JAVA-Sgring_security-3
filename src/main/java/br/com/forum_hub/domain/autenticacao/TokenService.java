package br.com.forum_hub.domain.autenticacao;

import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    public String gerarToken(Usuario user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("123456");
            return JWT.create()
                    .withIssuer("Forum Hub")
                    .withSubject(user.getUsername())
                    .withExpiresAt(expiracao(24))
                    .sign(algorithm);
        } catch (JWTCreationException e){
            System.out.println("ERRO JWT: " + e.getMessage());
            throw new RegraDeNegocioException("Falha na geracao do token JWT de acesso!!");
        }
    }

    public String gererRefreshToken(Usuario principal) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("123456");
            return JWT.create()
                    .withIssuer("Forum Hub")
                    .withSubject(principal.getId().toString())
                    .withExpiresAt(expiracao(120))
                    .sign(algorithm);
        } catch (JWTCreationException e){
            System.out.println("ERRO JWT: " + e.getMessage());
            throw new RegraDeNegocioException("Falha na geracao do token JWT de acesso!!");
        }
    }

    public String verificarToken(String token){
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256("123456");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Forum Hub")
                    .build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e){
            System.out.println("ERRO JWT: " + e.getMessage());
            throw new RegraDeNegocioException("Erro ao verificar token JWT de acesso!");
        }
    }


    private Instant expiracao(Integer i) {
        return LocalDateTime.now().plusMinutes(i).toInstant(ZoneOffset.of("-03:00"));
    }


}
