package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.config.widget.FieldWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.IconButtonWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ListWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.MapWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ScrollableWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.TextAreaWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfigScreen extends Screen {
    private static final int HEADER_HEIGHT = 32;
    private static final int SEARCH_Y = 8;
    private static final int SEARCH_PADDING_X = 4;
    private static final int SEARCH_MIN_WIDTH = 64;
    private static final int SEARCH_PREFERRED_WIDTH = 132;
    private static final int SEARCH_NAV_BUTTON_SIZE = 15;
    private static final int SEARCH_NAV_GAP = 2;

    public static final Atlas ATLAS = new Atlas(Identifier.fromNamespaceAndPath(ShatterLib.MOD_ID, "textures/config/config_atlas.png"), 263, 76);
    public static final Identifier LOGO = Identifier.fromNamespaceAndPath(ShatterLib.MOD_ID, "textures/config/logo.png");

    ShatterConfig config;
    Screen prevScreen;
    GenericObjectWidget object;
    ScrollableWidget scrollingObject;
    protected String searchQuery = "";
    protected TextAreaWidget searchWidget;
    protected IconButtonWidget<AbstractWidget> searchPrevButton;
    protected IconButtonWidget<AbstractWidget> searchNextButton;
    protected final List<FieldWidget> searchMatches = new ArrayList<>();
    protected int currentSearchMatchIndex = -1;

    public ConfigScreen(ShatterConfig config, Screen prevScreen) {
        super(Component.empty());
        this.prevScreen = prevScreen;

        if (config != null) {
            this.config = config;

            object = new GenericObjectWidget(config, null, new Annotation[]{}, null, null, () -> config, conf -> {
            });
            scrollingObject = new ScrollableWidget(0, HEADER_HEIGHT, this.width, this.height - HEADER_HEIGHT, object);
            scrollingObject.setPinScrollbarToScreenRight(true);
            scrollingObject.setScrollbarRightInset(0);
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
        this.setFocused(null);

        if (config != null) {
            config.save(Platform.getConfigFolder());
        }

        this.minecraft.setScreen(prevScreen);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (searchWidget != null) {
            if (event.hasControlDownWithQuirk() && event.key() == GLFW.GLFW_KEY_F) {
                searchWidget.setFocused(true);
                this.setFocused(searchWidget);
                return true;
            }

            if (event.key() == GLFW.GLFW_KEY_F3) {
                if (Minecraft.getInstance().hasShiftDown()) {
                    focusPreviousSearchMatch();
                } else {
                    focusNextSearchMatch();
                }
                return true;
            }

            if (searchWidget.isFocused() && event.key() == GLFW.GLFW_KEY_ENTER) {
                if (Minecraft.getInstance().hasShiftDown()) {
                    focusPreviousSearchMatch();
                } else {
                    focusNextSearchMatch();
                }
                return true;
            }

            if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
                if (!searchQuery.isEmpty()) {
                    searchWidget.setValue("");
                    searchWidget.setFocused(true);
                    this.setFocused(searchWidget);
                    return true;
                }

                if (searchWidget.isFocused()) {
                    searchWidget.setFocused(false);
                    if (this.getFocused() == searchWidget) {
                        this.setFocused(null);
                    }
                    return true;
                }
            }
        }

        return super.keyPressed(event);
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

        searchPrevButton = new IconButtonWidget<>(0, 0, SEARCH_NAV_BUTTON_SIZE, SEARCH_NAV_BUTTON_SIZE, this::focusPreviousSearchMatch, UIElements.ICON_UP);
        searchNextButton = new IconButtonWidget<>(0, 0, SEARCH_NAV_BUTTON_SIZE, SEARCH_NAV_BUTTON_SIZE, this::focusNextSearchMatch, UIElements.ICON_DOWN);
        this.addRenderableWidget(searchPrevButton);
        this.addRenderableWidget(searchNextButton);
        updateSearchControlsState();
    }

    protected void repositionSearchWidget() {
        if (searchWidget == null) {
            return;
        }

        int right = getContentRight() - SEARCH_PADDING_X;
        int nextButtonX = right - SEARCH_NAV_BUTTON_SIZE;
        int prevButtonX = nextButtonX - SEARCH_NAV_GAP - SEARCH_NAV_BUTTON_SIZE;
        int searchRight = prevButtonX - SEARCH_NAV_GAP;
        int searchLeftLimit = getContentLeft() + SEARCH_PADDING_X;
        int availableSearchWidth = Math.max(1, searchRight - searchLeftLimit);
        int width = Math.min(SEARCH_PREFERRED_WIDTH, availableSearchWidth);
        width = Math.max(SEARCH_MIN_WIDTH, width);
        if (width > availableSearchWidth) {
            width = availableSearchWidth;
        }

        int searchX = searchRight - width;
        searchWidget.setPosition(searchX, SEARCH_Y);
        searchWidget.setWidth(width);
        int navButtonsY = SEARCH_Y - 2;

        if (searchPrevButton != null) {
            searchPrevButton.setPosition(prevButtonX, navButtonsY);
        }

        if (searchNextButton != null) {
            searchNextButton.setPosition(nextButtonX, navButtonsY);
        }
    }

    protected void applySearchQuery() {
        if (object == null) {
            return;
        }

        object.applySearchHighlight(searchQuery);
        // Keep all entries visible in "find" mode.
        object.setSearchQuery("");
        rebuildSearchMatches(true);

        if (scrollingObject != null) {
            scrollingObject.visible = true;
            scrollingObject.active = true;
        }
    }

    private void rebuildSearchMatches(boolean jumpToFirst) {
        searchMatches.clear();

        String normalizedQuery = normalizeSearchQuery(searchQuery);
        if (!normalizedQuery.isEmpty() && object != null) {
            searchMatches.addAll(object.collectSearchMatches(normalizedQuery));
        }

        if (searchMatches.isEmpty()) {
            currentSearchMatchIndex = -1;
        } else if (jumpToFirst || currentSearchMatchIndex < 0 || currentSearchMatchIndex >= searchMatches.size()) {
            currentSearchMatchIndex = 0;
            focusCurrentSearchMatch();
        } else {
            currentSearchMatchIndex = Math.clamp(currentSearchMatchIndex, 0, searchMatches.size() - 1);
        }

        updateSearchControlsState();
    }

    private void focusNextSearchMatch() {
        if (searchMatches.isEmpty()) {
            updateSearchControlsState();
            return;
        }

        currentSearchMatchIndex = (currentSearchMatchIndex + 1) % searchMatches.size();
        focusCurrentSearchMatch();
        updateSearchControlsState();
    }

    private void focusPreviousSearchMatch() {
        if (searchMatches.isEmpty()) {
            updateSearchControlsState();
            return;
        }

        currentSearchMatchIndex = (currentSearchMatchIndex - 1 + searchMatches.size()) % searchMatches.size();
        focusCurrentSearchMatch();
        updateSearchControlsState();
    }

    private void focusCurrentSearchMatch() {
        if (object == null || scrollingObject == null || searchMatches.isEmpty()) {
            return;
        }

        int clampedIndex = Math.clamp(currentSearchMatchIndex, 0, searchMatches.size() - 1);
        FieldWidget match = searchMatches.get(clampedIndex);
        currentSearchMatchIndex = clampedIndex;

        expandParentsForMatch(match);
        object.repositionElements();
        scrollingObject.maxScrollY = Math.max(0, object.getHeight() - scrollingObject.getHeight());

        int topMargin = 8;
        int matchYInObject = match.getY() - object.getY();
        double targetOffset = -(matchYInObject - topMargin);
        scrollingObject.scrollToOffset(targetOffset, true);
        object.setFocused(match);
    }

    private void expandParentsForMatch(FieldWidget match) {
        Object current = match.getParent();
        while (current != null) {
            if (current instanceof GenericObjectWidget objectWidget) {
                objectWidget.setCollapsed(false);
            } else if (current instanceof ListWidget<?> listWidget) {
                listWidget.setCollapsed(false);
            } else if (current instanceof MapWidget<?> mapWidget) {
                mapWidget.setCollapsed(false);
            }

            if (current instanceof AbstractEntryWidget<?> entryWidget) {
                current = entryWidget.getParent();
            } else {
                current = null;
            }
        }
    }

    private void updateSearchControlsState() {
        boolean hasMatches = !searchMatches.isEmpty();
        if (searchPrevButton != null) {
            searchPrevButton.active = hasMatches;
        }

        if (searchNextButton != null) {
            searchNextButton.active = hasMatches;
        }
    }

    private static String normalizeSearchQuery(String query) {
        if (query == null) {
            return "";
        }

        return query.toLowerCase(Locale.ROOT).trim();
    }
}
