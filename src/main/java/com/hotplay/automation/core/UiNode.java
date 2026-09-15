package com.hotplay.automation.core;

public class UiNode {
    public final String resourceId;
    public final String text;
    public final String className;
    public final String bounds;
    public final boolean enabled;
    public final boolean checked;

    public UiNode(String resourceId, String text, String className,
                  String bounds, boolean enabled, boolean checked) {
        this.resourceId = resourceId;
        this.text       = text;
        this.className  = className;
        this.bounds     = bounds;
        this.enabled    = enabled;
        this.checked    = checked;
    }

    @Override
    public String toString() {
        return "UiNode{" + className + ", id='" + resourceId + "', text='" + text
                + "', bounds=" + bounds + ", enabled=" + enabled + ", checked=" + checked + "}";
    }
}