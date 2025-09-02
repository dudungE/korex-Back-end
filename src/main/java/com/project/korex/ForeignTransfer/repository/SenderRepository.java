package com.project.korex.ForeignTransfer.repository;

import com.project.korex.ForeignTransfer.entity.Sender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderRepository extends JpaRepository<Sender, Long> {
}
