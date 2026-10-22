package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
