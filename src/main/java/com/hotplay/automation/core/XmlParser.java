package com.hotplay.automation.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XmlParser {
    private XmlParser() {}

    private static final Pattern BOUNDS =
            Pattern.compile("\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]");

    /** Parse a uiautomator XML dump and return the root UiNode (the first <node>). */
    public static UiNode parse(String xml) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            NodeList nodes = doc.getElementsByTagName("node");
            if (nodes.getLength() == 0) return null;
            // The first <node> element is the root (a <hierarchy> can only have one).
            Element rootEl = (Element) nodes.item(0);
            // Wrap it in a synthetic parent so its children are discoverable
            // even when the root itself is not the node we want.
            UiNode root = toUiNode(rootEl);
            attachChildren(rootEl, root);
            return root;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse UI XML: " + e.getMessage(), e);
        }
    }

    private static void attachChildren(Element el, UiNode parent) {
        NodeList kids = el.getChildNodes();
        for (int i = 0; i < kids.getLength(); i++) {
            Node n = kids.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && "node".equals(n.getNodeName())) {
                Element ce = (Element) n;
                UiNode child = toUiNode(ce);
                parent.addChild(child);
                attachChildren(ce, child);
            }
        }
    }

    private static UiNode toUiNode(Element e) {
        int[] b = parseBounds(e.getAttribute("bounds"));
        return new UiNode(
                attr(e, "resource-id"),
                attr(e, "text"),
                attr(e, "content-desc"),
                attr(e, "class"),
                attr(e, "package"),
                bool(e, "enabled"),
                bool(e, "clickable"),
                bool(e, "focusable"),
                bool(e, "focused"),
                bool(e, "checked"),
                bool(e, "checkable"),
                bool(e, "selected"),
                bool(e, "scrollable"),
                b[0], b[1], b[2], b[3]);
    }

    private static String attr(Element e, String name) {
        String v = e.getAttribute(name);
        return v == null ? "" : v;
    }

    private static boolean bool(Element e, String name) {
        return "true".equals(e.getAttribute(name));
    }

    private static int[] parseBounds(String s) {
        Matcher m = BOUNDS.matcher(s == null ? "" : s);
        if (m.find()) return new int[]{
                Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))};
        return new int[]{0, 0, 0, 0};
    }
}