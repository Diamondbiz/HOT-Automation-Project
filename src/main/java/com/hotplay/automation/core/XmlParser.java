package com.hotplay.automation.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    public static List<UiNode> parse(String xml) {
        List<UiNode> out = new ArrayList<>();
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            collect(doc.getDocumentElement(), out);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse UI XML", e);
        }
        return out;
    }

    private static void collect(Element el, List<UiNode> out) {
        if ("node".equals(el.getTagName())) {
            out.add(new UiNode(
                    attr(el, "resource-id"),
                    attr(el, "text"),
                    attr(el, "class"),
                    attr(el, "bounds"),
                    Boolean.parseBoolean(attr(el, "enabled")),
                    Boolean.parseBoolean(attr(el, "checked"))
            ));
        }
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node c = children.item(i);
            if (c.getNodeType() == Node.ELEMENT_NODE) collect((Element) c, out);
        }
    }

    private static String attr(Element el, String name) {
        return el.hasAttribute(name) ? el.getAttribute(name) : "";
    }
}