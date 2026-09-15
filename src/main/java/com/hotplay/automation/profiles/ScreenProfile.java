package com.hotplay.automation.profiles;

import java.util.*;

public class ScreenProfile {
    public static class Element {
        public final String human;
        public final ScreenMarker.Type type;
        public final String value;
        public Element(String human, ScreenMarker.Type t, String v) {
            this.human = human; this.type = t; this.value = v;
        }
    }
    public static class Crop {
        public final String name;
        public final int[] bounds;      // [x1,y1,x2,y2]
        public final String baseline;   // absolute path to PNG
        public Crop(String name, int[] b, String baseline) {
            this.name = name; this.bounds = b; this.baseline = baseline;
        }
    }

    public final String name;
    public final ScreenMarker marker;
    public final List<Element> elements = new ArrayList<>();
    public final List<Crop>    crops    = new ArrayList<>();

    public ScreenProfile(String name, ScreenMarker marker) {
        this.name = name; this.marker = marker;
    }

    public static class Builder {
        private final ScreenProfile p;
        public Builder(String name, ScreenMarker.Type t, String v) {
            p = new ScreenProfile(name, new ScreenMarker(t, v));
        }
        public Builder element(String human, ScreenMarker.Type t, String v) {
            p.elements.add(new Element(human, t, v)); return this;
        }
        public Builder crop(String name, int[] bounds, String baseline) {
            p.crops.add(new Crop(name, bounds, baseline)); return this;
        }
        public ScreenProfile build() { return p; }
    }
}