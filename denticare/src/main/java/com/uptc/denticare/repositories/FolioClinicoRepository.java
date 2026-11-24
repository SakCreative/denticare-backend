package com.uptc.denticare.repositories;

import com.uptc.denticare.models.FolioClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolioClinicoRepository extends JpaRepository<FolioClinico, Long> {

    // Lista todos los folios de una historia clínica, desde el más nuevo al más viejo
    List<FolioClinico> findByHistoriaClinica_IdHistoriaOrderByFechaCreacionDesc(Long idHistoria);

}