package com.tbemerencio.catalog.repositories;

import com.tbemerencio.catalog.entities.Category;
import com.tbemerencio.catalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p " + // Tabele N:N podem ter repetições, usar DISTINCT
            "INNER JOIN p.categories c " +
            "WHERE (:categoryId IS NULL OR :categoryId IN c) " +// consulta N:N INNER JOIN/IN se a categoria for null ou existir na lista
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%')) )") // concatena explicitamente o valor do like %
    Page<Product> findProduct(Long categoryId, String name, Pageable pageable);

    // Para não gerar N+1 consulta
    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.categories " + // Busca apenas as categorias associadas a lista de produtos recebida
            "WHERE p IN :products")
    List<Product> findProductWithCategory(List<Product> products);
}