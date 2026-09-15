package com.hotplay.automation.validators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Outcome of validating one ScreenProfile against one UI dump.
 */
public final class ScreenAssertionResult {

    public static final class MarkerResult {
        public final String markerName;
        public final String resourceId;
        public final boolean found;

        public MarkerResult(String markerName, String resourceId, boolean found) {
            this.markerName = markerName;
            this.resourceId = resourceId;
            this.found = found;
        }
    }

    private final String profileName;
    private final int totalNodes;
    private final List<MarkerResult> markers;

    public ScreenAssertionResult(String profileName, int totalNodes, List<MarkerResult> markers) {
        this.profileName = profileName;
        this.totalNodes = totalNodes;
        this.markers = Collections.unmodifiableList(new ArrayList<>(markers));
    }

    public boolean passed() {
        return markers.stream().allMatch(m -> m.found);
    }

    public long foundCount() {
        return markers.stream().filter(m -> m.found).count();
    }

    public int totalCount() {
        return markers.size();
    }

    public String profileName() {
        return profileName;
    }

    public List<MarkerResult> markers() {
        return markers;
    }

    public int totalNodes() {
        return totalNodes;
    }

    /** Prints a human-readable report to stdout. */
    public void printReport() {
        System.out.println();
        System.out.println("── Screen assertion: " + profileName + " ──");
        System.out.println("   Parsed " + totalNodes + " nodes");
        for (MarkerResult m : markers) {
            System.out.printf("   %s %-22s %s%n",
                    m.found ? "✅" : "❌",
                    m.markerName,
                    m.resourceId);
        }
        System.out.println("   " + foundCount() + "/" + totalCount() + " markers found");

        if (passed()) {
            System.out.println("✅ PASSED — " + profileName);
        } else {
            System.out.println("❌ FAILED — " + profileName);
        }
        System.out.println();
    }
}