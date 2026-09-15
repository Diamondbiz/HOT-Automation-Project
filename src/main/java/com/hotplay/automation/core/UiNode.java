package com.hotplay.automation.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single &lt;node .../&gt; from a uiautomator XML dump.
 *
 * Immutable. Build it via XmlParser.parse(xml) which returns the root node;
 * walk the tree with children() / findAll / findById / findFirstByText.
 */
public class UiNode {

    // ---- raw attributes ----
    public final String resourceId;
    public final String text;
    public final String contentDesc;
    public final String className;
    public final String packageName;

    // ---- boolean flags ----
    public final boolean enabled;
    public final boolean clickable;
    public final boolean focusable;
    public final boolean focused;
    public final boolean checked;
    public final boolean checkable;
    public final boolean selected;
    public final boolean scrollable;

    // ---- geometry ----
    public final int x1, y1, x2, y2;

    private final List<UiNode> children = new ArrayList<>();

    public UiNode(String resourceId, String text, String contentDesc,
                  String className, String packageName,
                  boolean enabled, boolean clickable, boolean focusable, boolean focused,
                  boolean checked, boolean checkable, boolean selected, boolean scrollable,
                  int x1, int y1, int x2, int y2) {
        this.resourceId = resourceId;
        this.text = text;
        this.contentDesc = contentDesc;
        this.className = className;
        this.packageName = packageName;
        this.enabled = enabled;
        this.clickable = clickable;
        this.focusable = focusable;
        this.focused = focused;
        this.checked = checked;
        this.checkable = checkable;
        this.selected = selected;
        this.scrollable = scrollable;
        this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
    }

    // ---- geometry helpers ------------------------------------------------

    public int[] bounds() { return new int[]{x1, y1, x2, y2}; }
    public int centerX()  { return (x1 + x2) / 2; }
    public int centerY()  { return (y1 + y2) / 2; }
    public int width()    { return x2 - x1; }
    public int height()   { return y2 - y1; }

    // ---- attribute tests (null-safe) -------------------------------------

    public boolean hasResourceId(String id) {
        return id != null && id.equals(resourceId);
    }
    public boolean hasText(String t) {
        return t != null && t.equals(text);
    }
    public boolean textContains(String needle) {
        return text != null && needle != null && text.contains(needle);
    }

    // ---- tree navigation -------------------------------------------------

    public List<UiNode> children() { return Collections.unmodifiableList(children); }

    void addChild(UiNode child) { children.add(child); }

    /** Depth-first search by resource-id. Returns null if not found. */
    public UiNode findById(String id) {
        if (hasResourceId(id)) return this;
        for (UiNode c : children) {
            UiNode hit = c.findById(id);
            if (hit != null) return hit;
        }
        return null;
    }

    /** Depth-first search by exact text. */
    public UiNode findByText(String t) {
        if (hasText(t)) return this;
        for (UiNode c : children) {
            UiNode hit = c.findByText(t);
            if (hit != null) return hit;
        }
        return null;
    }

    /** Depth-first search by substring of text. */
    public UiNode findByTextContaining(String needle) {
        if (textContains(needle)) return this;
        for (UiNode c : children) {
            UiNode hit = c.findByTextContaining(needle);
            if (hit != null) return hit;
        }
        return null;
    }

    /** Every node in the tree whose resource-id matches. */
    public List<UiNode> findAllById(String id) {
        List<UiNode> out = new ArrayList<>();
        collectById(this, id, out);
        return out;
    }

    private static void collectById(UiNode n, String id, List<UiNode> out) {
        if (n.hasResourceId(id)) out.add(n);
        for (UiNode c : n.children) collectById(c, id, out);
    }

    @Override public String toString() {
        return "UiNode{" + className + ", id=" + resourceId
                + ", text=" + text
                + ", bounds=[" + x1 + "," + y1 + "][" + x2 + "," + y2 + "]"
                + ", enabled=" + enabled + ", focused=" + focused + "}";
    }
}