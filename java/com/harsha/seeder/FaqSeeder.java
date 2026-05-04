package com.harsha.seeder;

import com.harsha.service.FaqEmbeddingService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FaqSeeder
 *
 * Runs AUTOMATICALLY when your Spring Boot app starts.
 * It checks if FAQs already exist in pgVector — if not, it seeds all 8 FAQs.
 *
 * This means you only need to run this once. On subsequent app restarts,
 * it detects existing data and skips seeding.
 *
 * To add/edit FAQs later: clear the table in PostgreSQL and restart the app.
 *   → DELETE FROM faq_embeddings;
 *   → Restart app → seeder runs again with new FAQs
 */
@Component
public class FaqSeeder implements ApplicationRunner {

    private final FaqEmbeddingService faqEmbeddingService;

    public FaqSeeder(FaqEmbeddingService faqEmbeddingService) {
        this.faqEmbeddingService = faqEmbeddingService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            // Check if FAQs already seeded — skip if yes
            if (faqEmbeddingService.hasData()) {
                System.out.println("✅ RuralFix FAQs already in pgVector. Skipping seed.");
                return;
            }

            System.out.println("🌱 Seeding RuralFix FAQs into pgVector...");
            System.out.println("   (This may take a moment — Ollama is generating embeddings)\n");

            // ─────────────────────────────────────────────────────────────
            // Your 8 FAQs — edit these anytime.
            // Key   = the question (this is what gets embedded for search)
            // Value = the answer   (this is what Mistral uses to respond)
            // ─────────────────────────────────────────────────────────────
            Map<String, String> faqs = new LinkedHashMap<>();

            faqs.put(
                    "What is RuralFix and who is it for?",
                    "RuralFix is an online grievance portal built for rural citizens of Karnataka. " +
                            "It allows you to report civic issues — such as broken roads, water supply failures, " +
                            "or electricity outages — directly to the concerned government department, and track " +
                            "the resolution status in real time."
            );

            faqs.put(
                    "What types of issues can I report?",
                    "You can report a wide range of civic problems including: damaged or waterlogged roads, " +
                            "drinking water supply disruptions, street light failures, drainage blockages, " +
                            "public property damage, and other infrastructure-related issues affecting your " +
                            "village or gram panchayat."
            );

            faqs.put(
                    "Do I need to create an account to report an issue?",
                    "Yes, a registered account is required to submit complaints. This ensures accountability " +
                            "and allows you to receive updates on your complaint. Registration is free and takes " +
                            "less than a minute using your mobile number or email address."
            );

            faqs.put(
                    "How do I track the status of my complaint?",
                    "After logging in, click on View Status in the navigation bar. You will see all your " +
                            "submitted complaints along with their current status — Pending, In Progress, or " +
                            "Resolved — and any remarks added by the responsible authority."
            );

            faqs.put(
                    "How long does it take for an issue to be resolved?",
                    "Resolution time varies depending on the type and severity of the issue. Minor complaints " +
                            "such as street light repairs are typically addressed within 3 to 7 working days. Larger " +
                            "infrastructure problems may take longer. You will receive status updates at every stage."
            );

            faqs.put(
                    "Who reviews and acts on my complaint?",
                    "Your complaint is routed to the relevant government department or gram panchayat officer " +
                            "based on the issue category and location. An assigned official reviews the complaint, " +
                            "takes action, and updates the status with a remark. This ensures full transparency."
            );

            faqs.put(
                    "What if my complaint is not resolved or is incorrectly closed?",
                    "If you feel your complaint was closed without proper resolution, you can escalate it " +
                            "through the portal. Persistent or unaddressed complaints are flagged for higher-level " +
                            "review to ensure no grievance goes unheard."
            );

            faqs.put(
                    "Is RuralFix available in Kannada?",
                    "We are actively working on full Kannada language support to ensure accessibility for " +
                            "all citizens across Karnataka. The platform currently supports English. A Kannada " +
                            "interface will be rolled out in an upcoming release."
            );

            // Seed each FAQ — generates embedding + saves to pgVector
            int count = 1;
            for (Map.Entry<String, String> entry : faqs.entrySet()) {
                faqEmbeddingService.saveFaq(entry.getKey(), entry.getValue());
                System.out.println("   ✓ [" + count++ + "/" + faqs.size() + "] " + entry.getKey());
            }

            System.out.println("\n🎉 All " + faqs.size() + " FAQs seeded into pgVector successfully!");

        } catch (Exception e) {
            // App still starts even if seeding fails — just logs the error
            System.err.println("❌ FAQ seeding failed: " + e.getMessage());
            System.err.println("   Make sure Ollama is running: OLLAMA_ORIGINS=* ollama serve");
            System.err.println("   And nomic-embed-text is pulled: ollama pull nomic-embed-text");
        }
    }
}