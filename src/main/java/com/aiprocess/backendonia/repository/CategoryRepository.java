package com.aiprocess.backendonia.repository;

import com.aiprocess.backendonia.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
