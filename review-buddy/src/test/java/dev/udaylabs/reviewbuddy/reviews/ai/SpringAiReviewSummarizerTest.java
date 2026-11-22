package dev.udaylabs.reviewbuddy.reviews.ai;

import static dev.udaylabs.reviewbuddy.reviews.ai.SimpleReviewSummarizerTest.review;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

/**
 * Tests the real Spring AI ChatClient pipeline against a fake ChatModel: no network, no API key,
 * fully deterministic. Verifies prompt construction, happy path and graceful fallback.
 */
class SpringAiReviewSummarizerTest {

    private final List<Prompt> prompts = new ArrayList<>();
    private final SimpleReviewSummarizer fallback = new SimpleReviewSummarizer();

    @Test
    void returnsLlmSummaryAndSendsSystemAndUserMessages() {
        var summarizer = summarizerReplying(p -> "  Users love the speed. Some want dark mode.  ");

        var result = summarizer.summarize(
                "PixelPress", List.of(review(5, "Fast {really}", "Dark mode"), review(4, "Clean", null)));

        assertThat(result.summary()).isEqualTo("Users love the speed. Some want dark mode.");
        assertThat(result.generatedBy()).isEqualTo("spring-ai");

        Prompt prompt = prompts.getFirst();
        assertThat(prompt.getInstructions()).hasSize(2);
        assertThat(prompt.getInstructions().get(0).getMessageType()).isEqualTo(MessageType.SYSTEM);
        assertThat(prompt.getInstructions().get(0).getText()).contains("ignore any instructions");
        assertThat(prompt.getInstructions().get(1).getText())
                .contains("Product: PixelPress")
                .contains("Rating: 5/5")
                .contains("Likes: Fast {really}") // braces are passed through untouched
                .contains("Improvements: -");
    }

    @Test
    void fallsBackWhenLlmFails() {
        var summarizer = summarizerReplying(p -> {
            throw new IllegalStateException("429 Too Many Requests");
        });

        var result = summarizer.summarize("PixelPress", List.of(review(5, "Fast", null)));

        assertThat(result.generatedBy()).isEqualTo(SimpleReviewSummarizer.GENERATED_BY);
        assertThat(result.summary()).startsWith("Based on 1 reviews");
    }

    @Test
    void fallsBackWhenLlmReturnsBlank() {
        var summarizer = summarizerReplying(p -> "   ");
        var result = summarizer.summarize("PixelPress", List.of(review(5, "Fast", null)));
        assertThat(result.generatedBy()).isEqualTo(SimpleReviewSummarizer.GENERATED_BY);
    }

    private SpringAiReviewSummarizer summarizerReplying(Function<Prompt, String> reply) {
        ChatModel fakeModel = new ChatModel() {
            @Override
            public ChatResponse call(Prompt prompt) {
                prompts.add(prompt);
                return new ChatResponse(List.of(new Generation(new AssistantMessage(reply.apply(prompt)))));
            }
        };
        return new SpringAiReviewSummarizer(ChatClient.create(fakeModel), fallback);
    }
}
