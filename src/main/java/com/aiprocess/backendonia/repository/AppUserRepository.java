package com.aiprocess.backendonia.repository;

import com.aiprocess.backendonia.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    List<AppUser> findAllByOrderByNameAsc();
}
