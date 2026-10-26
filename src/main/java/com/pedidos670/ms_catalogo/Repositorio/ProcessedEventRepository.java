package com.pedidos670.ms_catalogo.Repositorio;


import com.pedidos670.ms_catalogo.Entidades.ProcessedEvent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, UUID> {
}
