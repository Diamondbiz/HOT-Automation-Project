package com.hotplay.automation.utils;

import com.hotplay.automation.config.TestConfig;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public final class ImageComparator {
    private ImageComparator() {}

    public static boolean matches(String actual, String expected) {
        try {
            BufferedImage a = ImageIO.read(new File(actual));
            BufferedImage b = ImageIO.read(new File(expected));
            if (a == null || b == null) return false;
            if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) return false;
            long match = 0, total = (long) a.getWidth() * a.getHeight();
            int tol = TestConfig.PIXEL_TOLERANCE;
            for (int y = 0; y < a.getHeight(); y++)
                for (int x = 0; x < a.getWidth(); x++) {
                    int pa = a.getRGB(x, y), pb = b.getRGB(x, y);
                    int dr = Math.abs(((pa>>16)&0xFF) - ((pb>>16)&0xFF));
                    int dg = Math.abs(((pa>>8)&0xFF)  - ((pb>>8)&0xFF));
                    int db = Math.abs((pa&0xFF)       - (pb&0xFF));
                    if (dr <= tol && dg <= tol && db <= tol) match++;
                }
            double sim = (100.0 * match) / total;
            System.out.printf("  image similarity %.2f%%%n", sim);
            return sim >= TestConfig.SIMILARITY_THRESHOLD;
        } catch (Exception e) { return false; }
    }
}