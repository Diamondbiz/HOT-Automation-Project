package com.hotplay.automation.profiles;

public class ScreenMarker {
    public enum Type { RESOURCE_ID, TEXT, TEXT_PRESENT_ANYWHERE }
    public final Type type;
    public final String value;
    public ScreenMarker(Type t, String v) { this.type = t; this.value = v; }
}