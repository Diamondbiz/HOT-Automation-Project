package com.hotplay.automation.profiles;

/**
 * One expected element on a screen. Currently a resource-id;
 * future versions may add TEXT and TEXT_PRESENT_ANYWHERE variants.
 */
public final class ScreenMarker {

    private final String name;
    private final String resourceId;

    public ScreenMarker(String name, String resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String name() {
        return name;
    }

    public String resourceId() {
        return resourceId;
    }
}