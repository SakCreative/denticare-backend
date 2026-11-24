package com.uptc.denticare.repositories;

import com.uptc.denticare.models.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // 1. Búsqueda exacta por el consecutivo legal de la factura
    Optional<Factura> findByNumeroFactura(String numeroFactura);

    // 2. Historial de un paciente CON PAGINACIÓN (Ej: Mostrar de 10 en 10, no las 5,000 de golpe)
    Page<Factura> findByPaciente_IdPacienteOrderByFechaEmisionDesc(Long idPaciente, Pageable pageable);

    // 3. Control de Morosos: Buscar facturas por estado de pago ("PENDIENTE") con paginación
    Page<Factura> findByEstadoPagoOrderByFechaEmisionAsc(String estadoPago, Pageable pageable);

    // 4. Cierre de Caja (Arqueo): Listar facturas emitidas en un rango de fechas (ej: hoy de 8am a 6pm)
    Page<Factura> findByFechaEmisionBetweenOrderByFechaEmisionDesc(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    // 5. CONSULTA JPQL FINANCIERA: Sumar todo el dinero real que ingresó en un periodo de tiempo
    @Query("SELECT SUM(f.total) FROM Factura f WHERE f.estadoPago = 'PAGADA' AND f.fechaEmision BETWEEN :inicio AND :fin")
    BigDecimal calcularIngresosPorRangoDeFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // 6. Preparación para facturación electrónica: Buscar facturas que fallaron al enviarse a la DIAN
    Page<Factura> findByEstadoFiscal(String estadoFiscal, Pageable pageable);
}