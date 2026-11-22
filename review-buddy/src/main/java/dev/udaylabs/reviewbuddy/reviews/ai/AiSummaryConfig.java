package dev.udaylabs.reviewbuddy.reviews.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Picks the summarizer: Spring AI when a ChatModel is configured (profile "ai" + OPENAI_API_KEY),
 * otherwise the deterministic rule based one. The app therefore runs with zero AI setup.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AiSummaryProperties.class)
class AiSummaryConfig {

    private static final Logger log = LoggerFactory.getLogger(AiSummaryConfig.class);

    @Bean
    ReviewSummarizer reviewSummarizer(ObjectProvider<ChatClient.Builder> chatClientBuilder) {
        var fallback = new SimpleReviewSummarizer();
        ChatClient.Builder builder = chatClientBuilder.getIfAvailable();
        if (builder == null) {
            log.info("No Spring AI ChatModel configured -> using rule based review summaries");
            return fallback;
        }
        log.info("Spring AI ChatClient available -> using LLM review summaries");
        return new SpringAiReviewSummarizer(builder.build(), fallback);
    }
}
