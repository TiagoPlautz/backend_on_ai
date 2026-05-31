package com.aiprocess.backendonia.repository;

import com.aiprocess.backendonia.domain.ProposalRecord;
import com.aiprocess.backendonia.domain.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProposalRepository extends JpaRepository<ProposalRecord, UUID> {
    List<ProposalRecord> findByStatusOrderByUpdatedAtDesc(ProposalStatus status);
}
