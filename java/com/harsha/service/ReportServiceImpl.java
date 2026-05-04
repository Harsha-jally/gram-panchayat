package com.harsha.service;

import com.harsha.entity.Report;
import com.harsha.repository.ReportRepository;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public List<Report> getReportsByUser(String email) {
        return reportRepository.findByEmailOrderByTimestampDesc(email);
    }

    @Override
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    public Report saveReport(Report report, MultipartFile audioFile, MultipartFile imageFile) throws IOException {
        if (audioFile != null && !audioFile.isEmpty()) {
            report.setVoiceTranscription(transcribeAudio(audioFile.getBytes()));
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            report.setProblemImage(imageFile.getBytes());
        }
        return reportRepository.save(report);
    }

    @Override
    public void updateStatus(Long id, String status, String actionTaken) {
        reportRepository.findById(id).ifPresent(report -> {
            report.setStatus(status);
            report.setActionTaken(actionTaken);
            reportRepository.save(report);
        });
    }

    private String transcribeAudio(byte[] audioBytes) {
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)
                    .setLanguageCode("en-IN")
                    .addAlternativeLanguageCodes("kn-IN")
                    .setEnableAutomaticPunctuation(true)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioBytes))
                    .build();
            RecognizeResponse response = speechClient.recognize(config, audio);
            StringBuilder transcript = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                transcript.append(result.getAlternatives(0).getTranscript()).append(" ");
            }
            return transcript.toString().trim().isEmpty() ? "[No speech detected]" : transcript.toString().trim();
        } catch (Exception e) {
            return "[Transcription failed: " + e.getMessage() + "]";
        }
    }
}