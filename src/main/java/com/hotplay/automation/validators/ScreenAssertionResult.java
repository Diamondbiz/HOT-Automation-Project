package com.hotplay.automation.validators;

import java.util.*;

public class ScreenAssertionResult {
    public final String screenName;
    public final List<String> passes = new ArrayList<>();
    public final List<String> failures = new ArrayList<>();

    public ScreenAssertionResult(String screenName) { this.screenName = screenName; }

    public void pass(String what)  { passes.add(what); }
    public void fail(String what)  { failures.add(what); }
    public boolean passed()        { return failures.isEmpty(); }
    public List<String> failures() { return failures; }
    public String summary() {
        return screenName + ": " + passes.size() + " pass, "
                + failures.size() + " fail";
    }
}