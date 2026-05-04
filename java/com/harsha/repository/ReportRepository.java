package com.harsha.repository;

import com.harsha.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByEmailOrderByTimestampDesc(String email);
    List<Report> findByPincode(String pincode);
    List<Report> findByPincodeAndStatus(String pincode, String status);
}