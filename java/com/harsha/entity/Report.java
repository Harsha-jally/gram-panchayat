package com.harsha.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String aadhaar;
    private String state = "Karnataka";
    private String district;
    private String taluk;
    private String village;
    private String address;
    private String pincode;
    private String problemDomain;

    @Column(length = 1000)
    private String description;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] problemImage;

    @Column(columnDefinition = "TEXT")
    private String voiceTranscription;

    private String status = "PENDING";
    private String complaintCategory;
    private String actionTaken; // This stores the Rejection Reason
    private LocalDateTime timestamp = LocalDateTime.now();

    @Transient
    private MultipartFile imageFile;

    @Transient
    private MultipartFile audioFile;

    public boolean hasVoiceTranscription() {
        return voiceTranscription != null && !voiceTranscription.isBlank();
    }

    public boolean hasProblemImage() {
        return problemImage != null && problemImage.length > 0;
    }
}