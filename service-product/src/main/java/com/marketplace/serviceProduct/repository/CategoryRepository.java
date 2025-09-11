package com.marketplace.serviceProduct.repository;

import com.marketplace.serviceProduct.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Set<Category> findAllByIdIn(List<Long> ids);

}
