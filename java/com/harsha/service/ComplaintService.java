package com.harsha.service;

import com.harsha.dto.ComplaintDTO;
import com.harsha.entity.ComplaintStatus;

import java.util.List;
import java.util.Optional;

public interface ComplaintService {
    ComplaintDTO createComplaint(ComplaintDTO dto);
    List<ComplaintDTO> getAllComplaints();
    Optional<ComplaintDTO> getComplaintById(Long id);
    List<ComplaintDTO> getComplaintsByStatus(ComplaintStatus status);
    ComplaintDTO resolveComplaint(Long id);
    ComplaintDTO updateComplaint(Long id, ComplaintDTO dto);
    void deleteComplaint(Long id);
    List<ComplaintDTO> getComplaintsByWard(Integer wardNumber);
}