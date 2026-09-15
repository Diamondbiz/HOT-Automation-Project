package com.hotplay.automation.validators;

import com.hotplay.automation.core.UiNode;
import com.hotplay.automation.profiles.ScreenMarker;
import com.hotplay.automation.profiles.ScreenProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateless. Knows how to check any ScreenProfile against any UI dump.
 * Does not perform I/O, screenshots, or logging side effects beyond the result object.
 */
public final class ScreenValidator {

    private ScreenValidator() {}

    public static ScreenAssertionResult validate(ScreenProfile profile, List<UiNode> nodes) {
        List<ScreenAssertionResult.MarkerResult> results = new ArrayList<>();

        for (ScreenMarker marker : profile.markers()) {
            boolean found = nodes.stream()
                    .anyMatch(n -> marker.resourceId().equals(n.resourceId));
            results.add(new ScreenAssertionResult.MarkerResult(
                    marker.name(), marker.resourceId(), found));
        }

        return new ScreenAssertionResult(profile.name(), nodes.size(), results);
    }
}