package it.hurts.shatterbyte.shatterlib.client.config;

public class UIElements {
    public static final UISprite TOGGLE_THINGY = new UISprite.Single(ConfigScreen.ATLAS, 97, 16, 10, 10);

    public static final UISprite TOGGLE_DISABLED = new UISprite.Single(ConfigScreen.ATLAS, 108, 16, 20, 10);
    public static final UISprite TOGGLE_ENABLED = new UISprite.Single(ConfigScreen.ATLAS, 108, 27, 20, 10);

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

    public static final UISprite BUTTON_HOVERED = new UISprite.NineSlice(ConfigScreen.ATLAS,
            new UISprite.NineSlice.Region(72, 19, 4, 4),
            new UISprite.NineSlice.Region(77, 19, 1, 4),
            new UISprite.NineSlice.Region(79, 19, 4, 4),
            new UISprite.NineSlice.Region(72, 24, 4, 1),
            new UISprite.NineSlice.Region(77, 24, 1, 1),
            new UISprite.NineSlice.Region(79, 24, 4, 1),
            new UISprite.NineSlice.Region(72, 26, 4, 6),
            new UISprite.NineSlice.Region(77, 26, 1, 6),
            new UISprite.NineSlice.Region(79, 26, 4, 6)
    );

    public static final UISprite BUTTON_PRESSED = new UISprite.NineSlice(ConfigScreen.ATLAS,
            new UISprite.NineSlice.Region(86, 22, 3, 3),
            new UISprite.NineSlice.Region(90, 22, 1, 3),
            new UISprite.NineSlice.Region(92, 22, 3, 3),
            new UISprite.NineSlice.Region(86, 26, 3, 1),
            new UISprite.NineSlice.Region(90, 26, 1, 1),
            new UISprite.NineSlice.Region(92, 26, 3, 1),
            new UISprite.NineSlice.Region(86, 28, 3, 4),
            new UISprite.NineSlice.Region(90, 28, 1, 4),
            new UISprite.NineSlice.Region(92, 28, 3, 4)
    );

    public static final UISprite.Single ICON_RESET = new UISprite.Single(ConfigScreen.ATLAS, 128, 3, 9, 9);
}
