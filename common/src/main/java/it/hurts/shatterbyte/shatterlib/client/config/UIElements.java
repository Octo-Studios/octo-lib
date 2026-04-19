package it.hurts.shatterbyte.shatterlib.client.config;

public class UIElements {
    public static final UISprite TOGGLE_THINGY = new UISprite.Single(ConfigScreen.ATLAS, 97, 16, 10, 10);

    public static final UISprite TOGGLE_DISABLED = new UISprite.Single(ConfigScreen.ATLAS, 108, 16, 20, 10);
    public static final UISprite TOGGLE_ENABLED = new UISprite.Single(ConfigScreen.ATLAS, 108, 27, 20, 10);

    public static final UISprite SLIDER_FULL = new UISprite.Single(ConfigScreen.ATLAS, 24, 50, 225, 5);
    public static final UISprite SLIDER_EMPTY = new UISprite.Single(ConfigScreen.ATLAS, 24, 56, 225, 6);
    public static final UISprite SLIDER_THINGY = new UISprite.Single(ConfigScreen.ATLAS, 16, 50, 7, 9);

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
            new UISprite.NineSlice.Region(72, 18, 4, 5),
            new UISprite.NineSlice.Region(77, 18, 1, 5),
            new UISprite.NineSlice.Region(79, 18, 4, 5),
            new UISprite.NineSlice.Region(72, 24, 4, 1),
            new UISprite.NineSlice.Region(77, 24, 1, 1),
            new UISprite.NineSlice.Region(79, 24, 4, 1),
            new UISprite.NineSlice.Region(72, 26, 4, 6),
            new UISprite.NineSlice.Region(77, 26, 1, 6),
            new UISprite.NineSlice.Region(79, 26, 4, 6)
    );

    public static final UISprite BUTTON_PRESSED = new UISprite.NineSlice(ConfigScreen.ATLAS,
            new UISprite.NineSlice.Region(86, 20, 3, 5),
            new UISprite.NineSlice.Region(90, 20, 1, 5),
            new UISprite.NineSlice.Region(92, 20, 3, 5),
            new UISprite.NineSlice.Region(86, 26, 3, 1),
            new UISprite.NineSlice.Region(90, 26, 1, 1),
            new UISprite.NineSlice.Region(92, 26, 3, 1),
            new UISprite.NineSlice.Region(86, 28, 3, 4),
            new UISprite.NineSlice.Region(90, 28, 1, 4),
            new UISprite.NineSlice.Region(92, 28, 3, 4)
    );

    public static final UISprite TEXT_AREA = new UISprite.NineSlice(ConfigScreen.ATLAS,
            new UISprite.NineSlice.Region(24, 23, 4, 4),
            new UISprite.NineSlice.Region(29, 23, 1, 4),
            new UISprite.NineSlice.Region(31, 23, 4, 4),
            new UISprite.NineSlice.Region(24, 28, 4, 15),
            new UISprite.NineSlice.Region(29, 28, 1, 15),
            new UISprite.NineSlice.Region(31, 28, 4, 15),
            new UISprite.NineSlice.Region(24, 44, 4, 5),
            new UISprite.NineSlice.Region(29, 44, 1, 5),
            new UISprite.NineSlice.Region(31, 44, 4, 5)
    );

    public static final UISprite FRAME = new UISprite.NineSlice(ConfigScreen.ATLAS,
            new UISprite.NineSlice.Region(5, 3, 4, 4),
            new UISprite.NineSlice.Region(10, 3, 1, 4),
            new UISprite.NineSlice.Region(12, 3, 4, 4),
            new UISprite.NineSlice.Region(5, 8, 4, 24),
            new UISprite.NineSlice.Region(10, 8, 1, 24),
            new UISprite.NineSlice.Region(12, 8, 4, 24),
            new UISprite.NineSlice.Region(5, 33, 4, 5),
            new UISprite.NineSlice.Region(10, 33, 1, 5),
            new UISprite.NineSlice.Region(12, 33, 4, 5)
    );

    public static final UISprite SCROLLBAR_THINGY = new UISprite.NineSlice(ConfigScreen.ATLAS,
            new UISprite.NineSlice.Region(18, 3, 1, 3),
            new UISprite.NineSlice.Region(19,3,2,3),
            new UISprite.NineSlice.Region(21,3,1,3),
            new UISprite.NineSlice.Region(18,7,1,18),
            new UISprite.NineSlice.Region(19,7,2,18),
            new UISprite.NineSlice.Region(21,7,1,18),
            new UISprite.NineSlice.Region(18,26,1,4),
            new UISprite.NineSlice.Region(19,26,2,4),
            new UISprite.NineSlice.Region(21,26,1,4)
    );

    public static final UISprite.Single ICON_RESET = new UISprite.Single(ConfigScreen.ATLAS, 128, 3, 9, 9);
    public static final UISprite.Single ICON_UP = new UISprite.Single(ConfigScreen.ATLAS, 106, 3, 9, 9);
    public static final UISprite.Single ICON_DOWN = new UISprite.Single(ConfigScreen.ATLAS, 95, 3, 9, 9);
    public static final UISprite.Single ICON_MINUS = new UISprite.Single(ConfigScreen.ATLAS, 160, 3, 9, 9);
    public static final UISprite.Single ICON_PLUS = new UISprite.Single(ConfigScreen.ATLAS, 73, 3, 9, 9);
}
