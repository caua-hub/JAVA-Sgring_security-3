package br.com.forum_hub.domain.usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCaseAndVerificadoTrue(String username);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByToken(String codigo);

    Optional<Usuario> findByNomeUsuarioIgnoreCase(String nomeUsuario);

    @Modifying
    @Query("""
    UPDATE Usuario u
    SET
        u.nomeCompleto = COALESCE(:nomeCompleto, u.nomeCompleto),
        u.biografia = COALESCE(:biografia, u.biografia),
        u.miniBiografia = COALESCE(:miniBiografia, u.miniBiografia)
    WHERE u.id = :id
""")
    void atualizar(
            @Param("nomeCompleto") String nomeCompleto,
            @Param("biografia") String biografia,
            @Param("miniBiografia") String miniBiografia,
            @Param("id") Long id
    );
}
