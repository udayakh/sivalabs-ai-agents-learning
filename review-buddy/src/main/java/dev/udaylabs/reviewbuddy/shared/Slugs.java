package dev.udaylabs.reviewbuddy.shared;

import java.text.Normalizer;
import java.util.Locale;

/** Utility to turn free text ("Pixel Press!") into URL friendly slugs ("pixel-press"). */
public final class Slugs {

    private Slugs() {}

    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+)|(-+$)", "");
    }
}
