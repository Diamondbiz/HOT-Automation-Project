package com.hotplay.automation.profiles;

public final class LiveMosaicProfile {
    private LiveMosaicProfile() {}

    private static final String PKG = "com.applicaster.il.hotvod";

    public static ScreenProfile get() {
        return new ScreenProfile.Builder(
                "LiveMosaic",
                ScreenMarker.Type.RESOURCE_ID,
                PKG + ":id/all_channels_fragment_recycler_view")
                .element("Mosaic grid",    ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/all_channels_fragment_recycler_view")
                .element("Channel tile",   ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/item_view_holder_channel_container")
                .element("Tile title",     ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/item_view_holder_channel_title_text_view")
                .element("Top bar title 'הכל'",
                        ScreenMarker.Type.TEXT, "הכל")
                .element("Bottom nav bar", ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/next_tv_activity_bottom_navigation_recycler_view")
                .build();
    }
}