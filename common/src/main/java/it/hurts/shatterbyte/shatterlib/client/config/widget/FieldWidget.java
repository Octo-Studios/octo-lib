package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Name;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FieldWidget extends AbstractWidget implements ContainerEventHandler, Child<GenericObjectWidget>, DynamicallySized, PathContainerWidget {
    private static final int NAME_Y = 4;
    private static final int DESCRIPTION_Y = 14;
    private static final float DESCRIPTION_SCALE = 0.5f;
    private static final int CONTENT_PADDING = 4;
    private static final int ENTRY_DOWN_Y_BASE = 20;
    private static final int MULTILINE_ENTRY_EXTRA_SPACING = 2;

    GenericObjectWidget parent;
    GenericObjectWidget.FieldInfo info;
    protected String fieldName = "";
    AbstractEntryWidget<?> entryWidget;
    ResetFieldButtonWidget resetButton;

    Font font = Minecraft.getInstance().font;

    boolean dragging = false;
    GuiEventListener focused;
    private List<GuiEventListener> childListeners = List.of();

    FieldWidget() {
        super(0, 0, 16, 16, Component.empty());
    }

    @Override
    public void repositionElements() {
        this.setWidth(this.getParent().getWidth() - 4);
        int multilineExtraHeight = getMultilineDescriptionExtraHeight();
        int multilineEntryExtraOffset = multilineExtraHeight > 0 ? multilineExtraHeight + MULTILINE_ENTRY_EXTRA_SPACING : 0;

        boolean moveDown = false;
        resetButton.setPosition(this.width - 4 - this.resetButton.getWidth(), 1);
        if (entryWidget.getWidth() > (this.getWidth() - font.width(info.name()+": ") - 12 - resetButton.getWidth()) || entryWidget instanceof DynamicallySized) {
            moveDown = true;
        }

        if (entryWidget instanceof DynamicallySized stuffInside) {
            if (moveDown) {
                entryWidget.setWidth(this.width - 4);
            } else {
                entryWidget.setWidth(this.resetButton.getLocalX() - 8);
            }

            stuffInside.repositionElements();
        }

        if (moveDown) {
            entryWidget.setPosition(4, ENTRY_DOWN_Y_BASE + multilineEntryExtraOffset);
        } else {
            entryWidget.setPosition(resetButton.getLocalX() - 4 - this.entryWidget.getWidth(), 4);
        }

        int entryBottom = entryWidget.getHeight() + CONTENT_PADDING + entryWidget.getLocalY();
        int descriptionBottom = getDescriptionBottomY() + CONTENT_PADDING;
        this.setHeight(Math.max(16, Math.max(entryBottom, descriptionBottom)));
    }

    public void requestRelayout() {
        //this.repositionElements();

        if (parent != null) {
            parent.relayoutAndPropagate();
        }
    }

    @SneakyThrows
    public static @Nullable FieldWidget createFromField(ShatterConfig config, String path, Object parentObject, Field field, GenericObjectWidget parent) {
        FieldWidget fieldWidget = new FieldWidget();

        Class<?> clazz = parentObject.getClass();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);

        field.setAccessible(true);

        if (field.isAnnotationPresent(Exclude.class)) {
            return null;
        }

        int mods = field.getModifiers();
        if (Modifier.isTransient(mods) || Modifier.isStatic(mods)) {
            return null;
        }

        fieldWidget.fieldName = field.getName();

        String prettyName = field.isAnnotationPresent(Name.class)
                ? field.getAnnotation(Name.class).value()
                : AbstractEntryWidget.convertFromCamelCase(fieldWidget.fieldName);

        String description = field.isAnnotationPresent(Comment.class)
                ? field.getAnnotation(Comment.class).value()
                : "";

        fieldWidget.info = new GenericObjectWidget.FieldInfo(prettyName, description);
        fieldWidget.setParent(parent);

        AbstractEntryWidget<?> widget = AbstractEntryWidget.tryCreate(path, fieldWidget, config, privateLookup, field, parentObject, parent.getDefaultValue());

        if (widget == null) {
            return null;
        }

        fieldWidget.entryWidget = widget;
        fieldWidget.resetButton = new ResetFieldButtonWidget(widget);
        fieldWidget.resetButton.setParent(fieldWidget);
        fieldWidget.childListeners = List.of(fieldWidget.entryWidget, fieldWidget.resetButton);

        return fieldWidget;
    }

    @Override
    public @Nullable GenericObjectWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable GenericObjectWidget parent) {
        this.parent = parent;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(font, info.name(), this.getX() + CONTENT_PADDING, this.getY() + NAME_Y, 0xffffffff, true);
        renderDescription(guiGraphics);
        resetButton.render(guiGraphics, mouseX, mouseY, partialTick);
        entryWidget.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderDescription(GuiGraphics guiGraphics) {
        List<FormattedCharSequence> lines = getWrappedDescriptionLines();
        if (lines.isEmpty()) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.getX() + CONTENT_PADDING, this.getY() + DESCRIPTION_Y);
        guiGraphics.pose().scale(DESCRIPTION_SCALE);

        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(font, lines.get(i), 0, i * font.lineHeight, 0xff888888, true);
        }

        guiGraphics.pose().popMatrix();
    }

    private int getDescriptionBottomY() {
        int lineCount = getWrappedDescriptionLines().size();
        if (lineCount == 0) {
            return NAME_Y + font.lineHeight;
        }

        int scaledLineHeight = Math.max(1, Math.round(font.lineHeight * DESCRIPTION_SCALE));
        return DESCRIPTION_Y + lineCount * scaledLineHeight;
    }

    private int getMultilineDescriptionExtraHeight() {
        int lineCount = getWrappedDescriptionLines().size();
        if (lineCount <= 1) {
            return 0;
        }

        int scaledLineHeight = Math.max(1, Math.round(font.lineHeight * DESCRIPTION_SCALE));
        return (lineCount - 1) * scaledLineHeight;
    }

    private List<FormattedCharSequence> getWrappedDescriptionLines() {
        List<FormattedCharSequence> wrappedLines = new ArrayList<>();
        String description = info.description();
        if (description == null || description.isEmpty()) {
            return wrappedLines;
        }

        int availableWidth = Math.max(1, this.width - CONTENT_PADDING * 2 - resetButton.getWidth() - 4);
        int unscaledWrapWidth = Math.max(1, Math.round(availableWidth / DESCRIPTION_SCALE));

        String[] explicitLines = description.split("\\n", -1);
        for (String explicitLine : explicitLines) {
            if (explicitLine.isEmpty()) {
                wrappedLines.add(Component.empty().getVisualOrderText());
                continue;
            }

            List<FormattedCharSequence> split = font.split(Component.literal(explicitLine), unscaledWrapWidth);
            if (split.isEmpty()) {
                wrappedLines.add(Component.empty().getVisualOrderText());
            } else {
                wrappedLines.addAll(split);
            }
        }

        return wrappedLines;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public List<? extends GuiEventListener> children() {
        return childListeners;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        this.dragging = isDragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focused != focused) {
            if (this.focused != null) {
                this.focused.setFocused(false);
            }

            if (focused != null) {
                focused.setFocused(true);
            }

            this.focused = focused;
        }
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    public void moveToTheTop() {
        if (this.parent == null) {
            return;
        }

        this.parent.renderables.remove(this);
        this.parent.renderables.addFirst(this);

        if (this.parent.getParent() instanceof PathContainerWidget widget) {
            widget.moveToTheTop();
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        ContainerEventHandler.super.mouseClicked(event, isDoubleClick);
        return super.mouseClicked(event, isDoubleClick);
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

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.setFocused(null);
        }
    }

    @Override
    public String getPath() {
        if (parent == null) {
            return fieldName;
        }

        String parentFieldPath = parent.getFieldPath();

        if (parentFieldPath == null || parentFieldPath.isEmpty()) {
            return fieldName;
        }

        return parentFieldPath + "." + fieldName;
    }

    @Override
    public void playDownSound(SoundManager handler) {}
}
