package com.harsha.service;

import com.harsha.dto.ComplaintDTO;
import com.harsha.entity.Complaint;
import com.harsha.entity.ComplaintStatus;
import com.harsha.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Override
    public ComplaintDTO createComplaint(ComplaintDTO dto) {
        Complaint complaint = convertToEntity(dto);
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint = complaintRepository.save(complaint);
        return convertToDTO(complaint);
    }

    @Override
    public List<ComplaintDTO> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        return complaints.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ComplaintDTO> getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public List<ComplaintDTO> getComplaintsByStatus(ComplaintStatus status) {
        List<Complaint> complaints = complaintRepository.findByStatus(status);
        return complaints.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintDTO resolveComplaint(Long id) {
        Optional<Complaint> optionalComplaint = complaintRepository.findById(id);
        if (optionalComplaint.isPresent()) {
            Complaint complaint = optionalComplaint.get();
            complaint.setStatus(ComplaintStatus.RESOLVED);
            complaint.setResolvedAt(LocalDateTime.now());
            complaint = complaintRepository.save(complaint);
            return convertToDTO(complaint);
        }
        return null;
    }

    @Override
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    @Override
    public List<ComplaintDTO> getComplaintsByWard(Integer wardNumber) {
        List<Complaint> complaints = complaintRepository.findByWardNumber(wardNumber);
        return complaints.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper methods
    private Complaint convertToEntity(ComplaintDTO dto) {
        Complaint complaint = new Complaint();
        complaint.setName(dto.getName());
        complaint.setPhoneNumber(dto.getPhoneNumber());
        complaint.setAadhaarNumber(dto.getAadhaarNumber());
        complaint.setAddress(dto.getAddress());
        complaint.setCategory(dto.getCategory());
        complaint.setDescription(dto.getDescription());
        complaint.setStatus(dto.getStatus() != null ? dto.getStatus() : ComplaintStatus.OPEN);
        complaint.setWardNumber(dto.getWardNumber());
        return complaint;
    }

    private ComplaintDTO convertToDTO(Complaint complaint) {
        return ComplaintDTO.builder()
                .id(complaint.getId())
                .name(complaint.getName())
                .phoneNumber(complaint.getPhoneNumber())
                .aadhaarNumber(complaint.getAadhaarNumber())
                .address(complaint.getAddress())
                .category(complaint.getCategory())
                .description(complaint.getDescription())
                .status(complaint.getStatus())
                .wardNumber(complaint.getWardNumber())
                .createdAt(complaint.getCreatedAt())
                .resolvedAt(complaint.getResolvedAt())
                .build();
    }

    @Override
    public ComplaintDTO updateComplaint(Long id, ComplaintDTO dto) {
        Optional<Complaint> optionalComplaint = complaintRepository.findById(id);
        if (optionalComplaint.isPresent()) {
            Complaint complaint = optionalComplaint.get();
            if (dto.getName() != null) complaint.setName(dto.getName());
            if (dto.getPhoneNumber() != null) complaint.setPhoneNumber(dto.getPhoneNumber());
            if (dto.getAadhaarNumber() != null) complaint.setAadhaarNumber(dto.getAadhaarNumber());
            if (dto.getAddress() != null) complaint.setAddress(dto.getAddress());
            if (dto.getCategory() != null) complaint.setCategory(dto.getCategory());
            if (dto.getDescription() != null) complaint.setDescription(dto.getDescription());
            if (dto.getWardNumber() != null) complaint.setWardNumber(dto.getWardNumber());
            if (dto.getStatus() != null) complaint.setStatus(dto.getStatus());

            complaint = complaintRepository.save(complaint);
            return convertToDTO(complaint);
        }
        return null;
    }
}