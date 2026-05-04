package com.harsha.service;

import com.harsha.entity.Report;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    List<Report> getAllReports();
    Optional<Report> getReportById(Long id);
    Report saveReport(Report report, MultipartFile audioFile, MultipartFile imageFile) throws IOException;

    // Updated to support actionTaken for the rejection-box in status.html
    void updateStatus(Long id, String status, String actionTaken);

    List<Report> getReportsByUser(String email);
}