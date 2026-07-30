package com.github.konstantyn111.crashapi.util;

import java.util.regex.Pattern;

public final class ResponseMessageSanitizer {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern URL_PATTERN = Pattern.compile("\\b(?:https?:\\/\\/|www\\.)\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATH_PATTERN = Pattern.compile("(?:[A-Za-z]:\\\\|/)(?:[^\\s,;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\b(accessToken|refreshToken|token|password|secret|authorization)\\b\\s*[:=]\\s*([^,;\\s]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern BEARER_PATTERN = Pattern.compile("\\bBearer\\s+[A-Za-z0-9._-]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern STACK_TRACE_PATTERN = Pattern.compile("(?:\\b(?:Caused by|Exception|Error)\\b|\\bat\\s+\\S+\\(.*\\))", Pattern.CASE_INSENSITIVE);

    private ResponseMessageSanitizer() {
    }

    public static String sanitize(String rawMessage, String fallbackMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return fallbackMessage == null ? "操作失败" : fallbackMessage;
        }

        String sanitized = rawMessage.trim().replaceAll("\\s+", " ");
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("[REDACTED_EMAIL]");
        sanitized = BEARER_PATTERN.matcher(sanitized).replaceAll("Bearer [REDACTED]");
        sanitized = URL_PATTERN.matcher(sanitized).replaceAll("[REDACTED_URL]");
        sanitized = PATH_PATTERN.matcher(sanitized).replaceAll("[REDACTED_PATH]");
        sanitized = TOKEN_PATTERN.matcher(sanitized).replaceAll("$1: [REDACTED]");

        if (sanitized.isBlank()) {
            return fallbackMessage == null ? "操作失败" : fallbackMessage;
        }

        if (STACK_TRACE_PATTERN.matcher(sanitized).find()) {
            return fallbackMessage == null ? "操作失败" : fallbackMessage;
        }

        return sanitized;
    }
}
