package com.uptc.denticare.repositories;

import com.uptc.denticare.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //esta linea crea el "SELECT * FROM usuario WHERE username = ?"
    Optional<Usuario> findByUsername(String username);
    
}
