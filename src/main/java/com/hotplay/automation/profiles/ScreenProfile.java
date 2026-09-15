package com.hotplay.automation.profiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure data: describes what a screen should contain.
 * Immutable once built. No ADB, no XML, no assertions.
 */
public final class ScreenProfile {

    private final String name;
    private final List<ScreenMarker> markers;

    private ScreenProfile(String name, List<ScreenMarker> markers) {
        this.name = name;
        this.markers = Collections.unmodifiableList(markers);
    }

    public String name() {
        return name;
    }

    public List<ScreenMarker> markers() {
        return markers;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private final List<ScreenMarker> markers = new ArrayList<>();

        private Builder(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Profile name must not be empty");
            }
            this.name = name;
        }

        /** Adds a marker identified by resource-id. */
        public Builder marker(String name, String resourceId) {
            markers.add(new ScreenMarker(name, resourceId));
            return this;
        }

        public ScreenProfile build() {
            if (markers.isEmpty()) {
                throw new IllegalStateException(
                        "Profile '" + name + "' has no markers — nothing to assert");
            }
            return new ScreenProfile(name, markers);
        }
    }
}