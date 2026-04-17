package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ScrollableWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.TextAreaWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.lang.annotation.Annotation;

public class ConfigScreen extends Screen {
    private static final int HEADER_HEIGHT = 32;
    private static final int SEARCH_Y = 8;
    private static final int SEARCH_PADDING_X = 4;
    private static final int SEARCH_MIN_WIDTH = 40;

    public static final Atlas ATLAS = new Atlas(Identifier.fromNamespaceAndPath(ShatterLib.MOD_ID, "textures/config/config_atlas.png"), 263, 76);

    ShatterConfig config;
    Screen prevScreen;
    GenericObjectWidget object;
    ScrollableWidget scrollingObject;
    protected String searchQuery = "";
    protected TextAreaWidget searchWidget;

    public ConfigScreen(ShatterConfig config, Screen prevScreen) {
        super(Component.empty());
        this.prevScreen = prevScreen;

        if (config != null) {
            this.config = config;

            object = new GenericObjectWidget(config, null, new Annotation[]{}, null, null, () -> config, conf -> {
            });
            scrollingObject = new ScrollableWidget(0, HEADER_HEIGHT, this.width, this.height - HEADER_HEIGHT, object);
            this.addRenderableWidget(scrollingObject);
            ensureSearchWidget();
            applySearchQuery();

            repositionElements();
        }
    }

    @Override
    protected void init() {
        super.init();
        ensureSearchWidget();
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        repositionSearchWidget();

        if (scrollingObject == null || object == null) {
            return;
        }

        int contentLeft = getContentLeft();
        int contentWidth = Math.max(32, getContentRight() - contentLeft);

        scrollingObject.setX(contentLeft);
        scrollingObject.setY(HEADER_HEIGHT);
        scrollingObject.setWidth(contentWidth);
        scrollingObject.setHeight(this.height - HEADER_HEIGHT);
        object.setWidth(scrollingObject.getWidth());
        object.repositionElements();
        scrollingObject.maxScrollY = Math.max(0, object.getHeight() - scrollingObject.getHeight());
        object.clamp(-scrollingObject.maxScrollY, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        //guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ATLAS.location, 0, 0, 0, 0, 263, 76, 263, 76);
        //UIElements.TEST.render(guiGraphics, RenderPipelines.GUI_TEXTURED, 0, 0, mouseX, mouseY);
        //UIElements.SLIDER_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, mouseX, mouseY);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height,0xFF2C2B31);
        guiGraphics.fillGradient(0, (int) (this.height*0.75f), this.width, this.height, 0xFF2C2B31, 0xFF222226);
    }

    @Override
    public void onClose() {
        if (config != null) {
            config.save(Platform.getConfigFolder());
        }

        this.minecraft.setScreen(prevScreen);
    }

    protected int getContentLeft() {
        return 0;
    }

    protected int getContentRight() {
        return this.width;
    }

    protected void ensureSearchWidget() {
        if (searchWidget != null) {
            return;
        }

        searchWidget = new TextAreaWidget(
                null,
                String.class,
                new Annotation[]{},
                null,
                "",
                () -> searchQuery,
                query -> {
                    String newQuery = query == null ? "" : query;
                    if (newQuery.equals(this.searchQuery)) {
                        return;
                    }

                    this.searchQuery = newQuery;
                    applySearchQuery();
                }
        );
        searchWidget.setPredicate(s -> s.length() <= 128);
        searchWidget.setPlaceholder("Search...");
        this.addRenderableWidget(searchWidget);
    }

    protected void repositionSearchWidget() {
        if (searchWidget == null) {
            return;
        }

        int x = getContentLeft() + SEARCH_PADDING_X;
        int right = getContentRight() - SEARCH_PADDING_X;
        int width = Math.max(SEARCH_MIN_WIDTH, right - x);

        searchWidget.setPosition(x, SEARCH_Y);
        searchWidget.setWidth(width);
    }

    protected void applySearchQuery() {
        if (object == null) {
            return;
        }

        object.setSearchQuery(searchQuery);
    }
}
