package com.harsha.controller;

import com.harsha.service.FaqEmbeddingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * OllamaProxyController  (RAG-enabled version)
 *
 * Endpoint:  POST /api/ask
 * Body:      { "question": "user's question" }
 * Response:  text/event-stream (SSE - token by token)
 *
 * What happens when someone asks a question:
 *  Step 1 → Convert user's question to numbers (embedding) via Ollama
 *  Step 2 → Search pgVector for the 3 most relevant FAQs
 *  Step 3 → Inject those FAQs as context into Mistral's prompt
 *  Step 4 → Mistral answers based on YOUR data (not guesses)
 *  Step 5 → Stream the answer token-by-token back to the browser
 */
@RestController
@RequestMapping("/api")
public class OllamaProxyController {

    private static final String OLLAMA_GENERATE_URL = "http://localhost:11434/api/generate";
    private static final String CHAT_MODEL          = "mistral:latest";

    private final FaqEmbeddingService faqEmbeddingService;
    private final ObjectMapper mapper = new ObjectMapper();

    public OllamaProxyController(FaqEmbeddingService faqEmbeddingService) {
        this.faqEmbeddingService = faqEmbeddingService;
    }

    @PostMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> ask(@RequestBody AskRequest req) {

        StreamingResponseBody stream = outputStream -> {
            PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

            try {
                String userQuestion = req.question();

                // STEP 1 & 2: pgVector semantic search
                // Find top 3 FAQs most relevant to the user's question
                List<Map<String, String>> relevantFaqs =
                        faqEmbeddingService.findSimilarFaqs(userQuestion, 3);

                // STEP 3: Build RAG context
                StringBuilder context = new StringBuilder();
                for (Map<String, String> faq : relevantFaqs) {
                    context.append("Q: ").append(faq.get("question")).append("\n");
                    context.append("A: ").append(faq.get("answer")).append("\n\n");
                }

                // Build the full RAG prompt
                String ragPrompt =
                        "You are a helpful assistant for RuralFix, a Karnataka government " +
                                "civic grievance portal for rural citizens.\n\n" +
                                "Use ONLY the FAQ context below to answer the user's question. " +
                                "Keep your answer clear and concise (2-4 sentences). " +
                                "If the answer is not covered in the context, say: " +
                                "'I don't have specific information about that. Please contact support.'\n\n" +
                                "=== Relevant FAQ Context ===\n" +
                                context +
                                "============================\n\n" +
                                "User question: " + userQuestion;

                // STEP 4 & 5: Stream Mistral response
                ObjectNode requestBody = mapper.createObjectNode();
                requestBody.put("model", CHAT_MODEL);
                requestBody.put("prompt", ragPrompt);
                requestBody.put("stream", true);
                byte[] payload = mapper.writeValueAsBytes(requestBody);

                HttpURLConnection conn = (HttpURLConnection)
                        new URL(OLLAMA_GENERATE_URL).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5_000);
                conn.setReadTimeout(60_000);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.getOutputStream().write(payload);

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = reader.readLine()) != null && !line.isBlank()) {
                        JsonNode json = mapper.readTree(line);
                        String token = json.path("response").asText("");
                        if (!token.isEmpty()) {
                            writer.write("data: " + mapper.writeValueAsString(token) + "\n\n");
                            writer.flush();
                        }
                        if (json.path("done").asBoolean(false)) {
                            writer.write("event: done\ndata: {}\n\n");
                            writer.flush();
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                writer.write("event: error\ndata: \"" + errorMsg.replace("\"", "'") + "\"\n\n");
                writer.flush();
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("Cache-Control", "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(stream);
    }

    public record AskRequest(String question) {}
}