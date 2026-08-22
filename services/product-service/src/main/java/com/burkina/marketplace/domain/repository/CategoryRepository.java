package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = "parent")
    Optional<Category> findByIdWithParent(Long id);
}
