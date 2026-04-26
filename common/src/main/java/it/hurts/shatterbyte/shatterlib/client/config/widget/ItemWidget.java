package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemWidget extends AbstractEntryWidget<Item> implements ContainerEventHandler, PathContainerWidget, SearchHighlightAware {
    private static final int ICON_SIZE = 16;
    private static final int ICON_GAP = 4;
    private static final int TEXT_HEIGHT = 15;
    private static final int TEXT_Y = Math.max(0, (ICON_SIZE - TEXT_HEIGHT) / 2);

    private final TextAreaWidget textWidget;
    private String textValue;
    private boolean syncingText;
    private boolean dragging;
    private GuiEventListener focused;

    public ItemWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, Item defaultValue, Supplier<Item> getter, Consumer<Item> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 200, ICON_SIZE);

        Item initialValue = normalizeItem(this.getValue(), defaultValue);
        this.textValue = toItemId(initialValue);

        this.textWidget = new TextAreaWidget(config, String.class, new Annotation[0], this, this.textValue, () -> textValue, this::applyTypedValue);
        this.textWidget.setPlaceholder("minecraft:stone");
        this.textWidget.setPredicate(value -> value.length() <= 128);
        this.textWidget.setParent(this);
        this.textWidget.setOnBlur(this::commitOrRollbackText);

        if (this.getValue() != initialValue) {
            super.setValue(initialValue);
        }

        repositionElements();
    }

    private void applyTypedValue(String value) {
        this.textValue = value == null ? "" : value;

        if (syncingText) {
            return;
        }

        Item parsedItem = parseItem(this.textValue);
        if (parsedItem != null) {
            super.setValue(parsedItem);
        }
    }

    private void commitOrRollbackText() {
        Item parsedItem = parseItem(textValue);
        if (parsedItem == null) {
            syncTextFromItem(this.getValue());
            return;
        }

        super.setValue(parsedItem);
        syncTextFromItem(parsedItem);
    }

    private void syncTextFromItem(@Nullable Item item) {
        syncingText = true;
        textValue = toItemId(item);
        textWidget.setValue(textValue);
        syncingText = false;
    }

    @Override
    public void setValue(Item value) {
        Item normalized = normalizeItem(value, getDefaultValue());
        super.setValue(normalized);
        syncTextFromItem(normalized);
    }

    public void repositionElements() {
        textWidget.setPosition(ICON_SIZE + ICON_GAP, TEXT_Y);
        textWidget.setWidth(Math.max(20, this.getWidth() - ICON_SIZE - ICON_GAP));
        this.setHeight(ICON_SIZE);
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderItemIcon(guiGraphics);
        textWidget.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderItemIcon(GuiGraphics guiGraphics) {
        ItemStack stack = getPreviewStack();
        if (stack.isEmpty()) {
            return;
        }

        guiGraphics.renderItem(stack, this.getX(), this.getY());
    }

    private ItemStack getPreviewStack() {
        if (textValue != null && !textValue.isBlank()) {
            Item parsedItem = parseItem(textValue);
            if (parsedItem != null) {
                return new ItemStack(parsedItem);
            }

            return new ItemStack(Items.BARRIER);
        }

        Item currentValue = normalizeItem(this.getValue(), getDefaultValue());
        if (currentValue == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(currentValue);
    }

    private static @Nullable Item parseItem(@Nullable String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        try {
            Identifier id = Identifier.parse(normalized);
            if (!BuiltInRegistries.ITEM.containsKey(id)) {
                return null;
            }

            return BuiltInRegistries.ITEM.getValue(id);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static Item normalizeItem(@Nullable Item item, @Nullable Item fallback) {
        if (item != null) {
            return item;
        }

        if (fallback != null) {
            return fallback;
        }

        return Items.AIR;
    }

    private static String toItemId(@Nullable Item item) {
        return BuiltInRegistries.ITEM.getKey(normalizeItem(item, Items.AIR)).toString();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return ContainerEventHandler.super.mouseClicked(event, isDoubleClick) || super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        ContainerEventHandler.super.mouseReleased(event);
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        ContainerEventHandler.super.mouseDragged(event, mouseX, mouseY);
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        ContainerEventHandler.super.keyPressed(event);
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        ContainerEventHandler.super.keyReleased(event);
        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        ContainerEventHandler.super.charTyped(event);
        return super.charTyped(event);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.setFocused((GuiEventListener) null);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(textWidget);
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        dragging = isDragging;
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focused == focused) {
            return;
        }

        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (focused != null) {
            focused.setFocused(true);
        }

        this.focused = focused;
    }

    @Override
    public void setSearchHighlightQuery(@Nullable String query) {
        textWidget.setSearchHighlightQuery(query);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || textWidget.isMouseOver(mouseX, mouseY);
    }

    @Override
    public String getPath() {
        PathContainerWidget parent = this.getParent();
        return parent == null ? "" : parent.getPath();
    }

    @Override
    public void moveToTheTop() {
        PathContainerWidget parent = this.getParent();
        if (parent != null) {
            parent.moveToTheTop();
        }
    }

    @Override
    public void requestRelayout() {
        repositionElements();
        super.requestRelayout();
    }
}
