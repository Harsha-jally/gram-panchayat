package com.harsha.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FaqEmbeddingService {

    // Ollama endpoint for generating embeddings (number-lists from text)
    private static final String OLLAMA_EMBED_URL = "http://localhost:11434/api/embeddings";

    // This model converts text → 768 numbers. It's NOT for chat, only for embeddings.
    private static final String EMBED_MODEL = "nomic-embed-text";

    private final JdbcTemplate pgJdbcTemplate;  // talks to PostgreSQL
    private final RestTemplate restTemplate;     // makes HTTP calls to Ollama
    private final ObjectMapper mapper = new ObjectMapper();

    public FaqEmbeddingService(
            @Qualifier("pgJdbcTemplate") JdbcTemplate pgJdbcTemplate,
            RestTemplate restTemplate) {
        this.pgJdbcTemplate = pgJdbcTemplate;
        this.restTemplate = restTemplate;
    }

    public float[] generateEmbedding(String text) throws Exception {
        // Build the request body: { "model": "nomic-embed-text", "prompt": "text here" }
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", EMBED_MODEL);
        requestBody.put("prompt", text);

        // POST to Ollama, get response as a JSON string
        String response = restTemplate.postForObject(
                OLLAMA_EMBED_URL,
                requestBody,
                String.class
        );

        // Parse the JSON response and extract the "embedding" array
        // Response looks like: { "embedding": [0.23, -0.87, 0.45, ...] }
        JsonNode root = mapper.readTree(response);
        JsonNode embeddingArray = root.path("embedding");

        // Convert the JSON array to a Java float[]
        float[] embedding = new float[embeddingArray.size()];
        for (int i = 0; i < embeddingArray.size(); i++) {
            embedding[i] = (float) embeddingArray.get(i).asDouble();
        }

        return embedding;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. SAVE FAQ
    //    Generates embedding for the question and saves the full FAQ row
    //    into the faq_embeddings table in PostgreSQL
    // ─────────────────────────────────────────────────────────────────────────
    public void saveFaq(String question, String answer) throws Exception {
        // Convert the FAQ question into numbers
        float[] embedding = generateEmbedding(question);

        // Convert float[] to pgVector string format: [0.1,0.2,0.3,...]
        String vectorString = toVectorString(embedding);

        // Insert into PostgreSQL
        // ::vector is a pgVector cast — tells PostgreSQL this string is a vector
        pgJdbcTemplate.update(
                "INSERT INTO faq_embeddings (question, answer, embedding) VALUES (?, ?, ?::vector)",
                question,
                answer,
                vectorString
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. HAS DATA
    //    Returns true if FAQs already exist in the table.
    //    Used by FaqSeeder to avoid re-seeding on every app restart.
    // ─────────────────────────────────────────────────────────────────────────
    public boolean hasData() {
        Integer count = pgJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM faq_embeddings",
                Integer.class
        );
        return count != null && count > 0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. FIND SIMILAR FAQS
    //    Converts the user's question to numbers, then asks pgVector:
    //    "which stored FAQ numbers are closest to these?"
    //
    //    The <=> operator is pgVector's cosine distance.
    //    ORDER BY embedding <=> ?::vector means "sort by most similar first"
    //
    //    Returns a list of { "question": "...", "answer": "..." } maps
    // ─────────────────────────────────────────────────────────────────────────
    public List<Map<String, String>> findSimilarFaqs(String userQuestion, int limit) throws Exception {
        // Convert user's question to numbers
        float[] embedding = generateEmbedding(userQuestion);
        String vectorString = toVectorString(embedding);

        // Ask pgVector for the closest matching FAQs
        return pgJdbcTemplate.query(
                "SELECT question, answer " +
                        "FROM faq_embeddings " +
                        "ORDER BY embedding <=> ?::vector " +  // <=> = cosine distance, closest first
                        "LIMIT ?",
                (rs, rowNum) -> {
                    Map<String, String> faq = new LinkedHashMap<>();
                    faq.put("question", rs.getString("question"));
                    faq.put("answer", rs.getString("answer"));
                    return faq;
                },
                vectorString,
                limit
        );
    }

    private String toVectorString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}