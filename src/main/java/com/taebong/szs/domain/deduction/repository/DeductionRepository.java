package com.taebong.szs.domain.deduction.repository;

import com.taebong.szs.domain.deduction.vo.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {
}
