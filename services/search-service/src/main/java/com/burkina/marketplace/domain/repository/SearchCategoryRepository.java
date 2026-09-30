package com.burkina.marketplace.domain.repository;

import com.burkina.marketplace.domain.entity.SearchCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchCategoryRepository extends JpaRepository<SearchCategory, Long> {}
