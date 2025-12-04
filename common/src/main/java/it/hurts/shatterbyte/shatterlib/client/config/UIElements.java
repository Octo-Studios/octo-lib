package it.hurts.shatterbyte.shatterlib.client.config;

public class UIElements {
    public static final UISprite SLIDER_THINGY = new UISprite.Single(ConfigScreen.ATLAS, 54, 1, 7, 9);

    public static final UISprite BUTTON = new UISprite.NineSlice(ConfigScreen.ATLAS,
            new UISprite.NineSlice.Region(56, 18, 5, 5),
            new UISprite.NineSlice.Region(62, 18, 1, 5),
            new UISprite.NineSlice.Region(64, 18, 5, 5),
            new UISprite.NineSlice.Region(56, 24, 5, 1),
            new UISprite.NineSlice.Region(62, 24, 1, 1),
            new UISprite.NineSlice.Region(64, 24, 5, 1),
            new UISprite.NineSlice.Region(56, 26, 5, 6),
            new UISprite.NineSlice.Region(62, 26, 1, 6),
            new UISprite.NineSlice.Region(64, 26, 5, 6)
    );

    public static final UISprite TEST = new UISprite.NineSlice(ConfigScreen.ATLAS,
            new UISprite.NineSlice.Region(112, 16, 4, 4),
            new UISprite.NineSlice.Region(117, 16, 5, 4),
            new UISprite.NineSlice.Region(123, 16, 4, 4),
            new UISprite.NineSlice.Region(112, 21, 4, 5),
            new UISprite.NineSlice.Region(117, 21, 5, 5),
            new UISprite.NineSlice.Region(123, 21, 4, 5),
            new UISprite.NineSlice.Region(112, 27, 4, 4),
            new UISprite.NineSlice.Region(117, 27, 5, 4),
            new UISprite.NineSlice.Region(123, 27, 4, 4)
    );
}
