package com.github.konstantyn111.crashapi.util.admin;

public class SolutionUtils {
    public static String generateSolutionId() {
        return "s" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}