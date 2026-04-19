package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShatterColorWidget extends AbstractEntryWidget<ShatterColor> implements ContainerEventHandler, PathContainerWidget {
    private static final int ROW_H = 15;
    private static final int BTN_W = 15;
    private static final int ROW_GAP = 3;
    private static final int COLLAPSED_HEX_WIDTH = 200;
    private static final int DROP_GAP = 4;
    private static final int PAD = 5;
    private static final int COL_GAP = 6;
    private static final int FIELD_H = 15;
    private static final int FIELD_GAP = 3;
    private static final int LABEL_W = 36;
    private static final int MIN_LEFT = LABEL_W + 46;
    private static final float TEXT_COLUMN_EXPAND_FACTOR = 1.5625f;
    private static final int PICKER_MIN = 82;
    private static final int PICKER_MAX = 132;
    private static final int SV_H = 64;
    private static final int SLIDER_H = 8;
    private static final int SLIDER_GAP = 3;
    private static final int CHECKER_LIGHT = 0xff454550;
    private static final int CHECKER_DARK = 0xff2f3037;
    private static final int PANEL_COLOR = 0xff181820;
    private static final int PANEL_BORDER = 0xff1a1a22;
    private static final int LABEL_COLOR = 0xffb8b8c4;

    private final TextAreaWidget rowHexWidget;
    private final TextAreaWidget popupHexWidget;
    private final TextAreaWidget rgbRWidget;
    private final TextAreaWidget rgbGWidget;
    private final TextAreaWidget rgbBWidget;

    private final TextAreaWidget cmykCWidget;
    private final TextAreaWidget cmykMWidget;
    private final TextAreaWidget cmykYWidget;
    private final TextAreaWidget cmykKWidget;

    private final TextAreaWidget hsvHWidget;
    private final TextAreaWidget hsvSWidget;
    private final TextAreaWidget hsvVWidget;

    private final TextAreaWidget hslHWidget;
    private final TextAreaWidget hslSWidget;
    private final TextAreaWidget hslLWidget;
    private final IconButtonWidget<ShatterColorWidget> togglePopupButton;

    private String rowHexText = "";
    private String popupHexText = "";
    private String rgbRText = "";
    private String rgbGText = "";
    private String rgbBText = "";

    private String cmykCText = "";
    private String cmykMText = "";
    private String cmykYText = "";
    private String cmykKText = "";

    private String hsvHText = "";
    private String hsvSText = "";
    private String hsvVText = "";

    private String hslHText = "";
    private String hslSText = "";
    private String hslLText = "";

    private float hue;
    private float saturation;
    private float value;
    private float alpha;

    private boolean expanded;
    private boolean syncingFields;
    private DragTarget dragTarget = DragTarget.NONE;
    private boolean dragging;
    private GuiEventListener focused;

    public ShatterColorWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, ShatterColor defaultValue, Supplier<ShatterColor> getter, Consumer<ShatterColor> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 200, ROW_H);

        rowHexWidget = createTextEditor(() -> rowHexText, text -> {
            rowHexText = text;
            applyHexField(text);
        });
        rowHexWidget.setPredicate(ShatterColorWidget::isHexLikeInput);
        rowHexWidget.setPlaceholder("#RRGGBB");
        rowHexWidget.setParent(this);

        popupHexWidget = createTextEditor(() -> popupHexText, text -> {
            popupHexText = text;
            applyHexField(text);
        });
        popupHexWidget.setPredicate(ShatterColorWidget::isHexLikeInput);
        popupHexWidget.setPlaceholder("#RRGGBB / #AARRGGBB");
        popupHexWidget.setParent(this);

        rgbRWidget = createNumericTextEditor(() -> rgbRText, text -> {
            rgbRText = text;
            applyRgbFields();
        }, "R");
        rgbRWidget.setParent(this);
        rgbGWidget = createNumericTextEditor(() -> rgbGText, text -> {
            rgbGText = text;
            applyRgbFields();
        }, "G");
        rgbGWidget.setParent(this);
        rgbBWidget = createNumericTextEditor(() -> rgbBText, text -> {
            rgbBText = text;
            applyRgbFields();
        }, "B");
        rgbBWidget.setParent(this);

        cmykCWidget = createNumericTextEditor(() -> cmykCText, text -> {
            cmykCText = text;
            applyCmykFields();
        }, "C");
        cmykCWidget.setParent(this);
        cmykMWidget = createNumericTextEditor(() -> cmykMText, text -> {
            cmykMText = text;
            applyCmykFields();
        }, "M");
        cmykMWidget.setParent(this);
        cmykYWidget = createNumericTextEditor(() -> cmykYText, text -> {
            cmykYText = text;
            applyCmykFields();
        }, "Y");
        cmykYWidget.setParent(this);
        cmykKWidget = createNumericTextEditor(() -> cmykKText, text -> {
            cmykKText = text;
            applyCmykFields();
        }, "K");
        cmykKWidget.setParent(this);

        hsvHWidget = createNumericTextEditor(() -> hsvHText, text -> {
            hsvHText = text;
            applyHsvFields();
        }, "H");
        hsvHWidget.setParent(this);
        hsvSWidget = createNumericTextEditor(() -> hsvSText, text -> {
            hsvSText = text;
            applyHsvFields();
        }, "S");
        hsvSWidget.setParent(this);
        hsvVWidget = createNumericTextEditor(() -> hsvVText, text -> {
            hsvVText = text;
            applyHsvFields();
        }, "V");
        hsvVWidget.setParent(this);

        hslHWidget = createNumericTextEditor(() -> hslHText, text -> {
            hslHText = text;
            applyHslFields();
        }, "H");
        hslHWidget.setParent(this);
        hslSWidget = createNumericTextEditor(() -> hslSText, text -> {
            hslSText = text;
            applyHslFields();
        }, "S");
        hslSWidget.setParent(this);
        hslLWidget = createNumericTextEditor(() -> hslLText, text -> {
            hslLText = text;
            applyHslFields();
        }, "L");
        hslLWidget.setParent(this);

        togglePopupButton = new IconButtonWidget<>(0, 0, BTN_W, BTN_W, this::toggleExpanded, null);
        togglePopupButton.setParent(this);

        ShatterColor initial = this.getValue();
        if (initial == null) {
            initial = defaultValue != null ? defaultValue : ShatterColor.WHITE;
            super.setValue(initial);
        }

        syncFromColor(initial);
        syncTextFieldsFromColor();
        repositionElements();
    }

    private TextAreaWidget createTextEditor(Supplier<String> getter, Consumer<String> setter) {
        TextAreaWidget editor = new TextAreaWidget(this.getConfig(), String.class, new Annotation[0], this, "", getter, setter);
        editor.setPredicate(s -> s.length() <= 48);
        return editor;
    }

    private TextAreaWidget createNumericTextEditor(Supplier<String> getter, Consumer<String> setter, String placeholder) {
        TextAreaWidget editor = createTextEditor(getter, setter);
        editor.setPlaceholder(placeholder);
        return editor;
    }

    public void repositionElements() {
        int rowHexWidth = getRowHexWidth();
        rowHexWidget.setPosition(0, 0);
        rowHexWidget.setWidth(rowHexWidth);
        togglePopupButton.setPosition(rowHexWidth + ROW_GAP, 0);

        PopupLayout layout = buildPopupLayout();
        popupHexWidget.setPosition(layout.fieldLocalX(), layout.hexFieldLocalY());
        popupHexWidget.setWidth(layout.fieldWidth());

        positionChannelRow(layout.fieldLocalX(), layout.rgbFieldLocalY(), layout.fieldWidth(), FIELD_GAP, rgbRWidget, rgbGWidget, rgbBWidget);
        positionChannelRow(layout.fieldLocalX(), layout.cmykFieldLocalY(), layout.fieldWidth(), FIELD_GAP, cmykCWidget, cmykMWidget, cmykYWidget, cmykKWidget);
        positionChannelRow(layout.fieldLocalX(), layout.hsvFieldLocalY(), layout.fieldWidth(), FIELD_GAP, hsvHWidget, hsvSWidget, hsvVWidget);
        positionChannelRow(layout.fieldLocalX(), layout.hslFieldLocalY(), layout.fieldWidth(), FIELD_GAP, hslHWidget, hslSWidget, hslLWidget);

        this.setHeight(expanded ? layout.totalHeight() : ROW_H);
    }

    private void positionChannelRow(int x, int y, int totalWidth, int gap, TextAreaWidget... widgets) {
        if (widgets.length == 0) {
            return;
        }

        int[] widths = splitWidths(totalWidth, widgets.length, gap);
        int cursor = x;
        for (int i = 0; i < widgets.length; i++) {
            widgets[i].setPosition(cursor, y);
            widgets[i].setWidth(widths[i]);
            cursor += widths[i] + gap;
        }
    }

    private static int[] splitWidths(int totalWidth, int count, int gap) {
        int[] widths = new int[count];
        if (count <= 0) {
            return widths;
        }

        int available = Math.max(count, totalWidth - gap * (count - 1));
        int base = available / count;
        int remainder = available % count;
        for (int i = 0; i < count; i++) {
            widths[i] = base + (i < remainder ? 1 : 0);
        }

        return widths;
    }

    @Override
    public void setValue(ShatterColor value) {
        applyColor(value);
    }

    private void applyColor(@Nullable ShatterColor color) {
        ShatterColor safe = color == null ? ShatterColor.WHITE : color;
        super.setValue(safe);
        syncFromColor(safe);
        syncTextFieldsFromColor();
    }

    private void syncFromColor(ShatterColor color) {
        float[] hsv = color.toHSV();
        hue = clamp01(hsv[0]);
        saturation = clamp01(hsv[1]);
        value = clamp01(hsv[2]);
        alpha = clamp01(color.a());
    }

    private void syncTextFieldsFromColor() {
        ShatterColor color = getCurrentColor();
        int[] rgb = rgbComponents(color);
        int[] cmyk = cmykComponents(color);
        int[] hsv = hsvComponents();
        int[] hsl = hslComponents(color);

        syncingFields = true;
        try {
            rowHexWidget.setValue(formatHex(color));
            popupHexWidget.setValue(formatHex(color));

            rgbRWidget.setValue(String.valueOf(rgb[0]));
            rgbGWidget.setValue(String.valueOf(rgb[1]));
            rgbBWidget.setValue(String.valueOf(rgb[2]));

            cmykCWidget.setValue(String.valueOf(cmyk[0]));
            cmykMWidget.setValue(String.valueOf(cmyk[1]));
            cmykYWidget.setValue(String.valueOf(cmyk[2]));
            cmykKWidget.setValue(String.valueOf(cmyk[3]));

            hsvHWidget.setValue(String.valueOf(hsv[0]));
            hsvSWidget.setValue(String.valueOf(hsv[1]));
            hsvVWidget.setValue(String.valueOf(hsv[2]));

            hslHWidget.setValue(String.valueOf(hsl[0]));
            hslSWidget.setValue(String.valueOf(hsl[1]));
            hslLWidget.setValue(String.valueOf(hsl[2]));
        } finally {
            syncingFields = false;
        }
    }

    private void applyHexField(String text) {
        if (syncingFields) {
            return;
        }

        ShatterColor parsed = parseHex(text);
        if (parsed != null) {
            applyColor(parsed);
        }
    }

    private void applyRgbFields() {
        if (syncingFields) {
            return;
        }

        Float r = parseDecimal(rgbRText);
        Float g = parseDecimal(rgbGText);
        Float b = parseDecimal(rgbBText);
        if (r == null || g == null || b == null) {
            return;
        }

        applyColor(new ShatterColor(normalizeRgb(r), normalizeRgb(g), normalizeRgb(b), alpha));
    }

    private void applyCmykFields() {
        if (syncingFields) {
            return;
        }

        Float c = parseDecimal(cmykCText);
        Float m = parseDecimal(cmykMText);
        Float y = parseDecimal(cmykYText);
        Float k = parseDecimal(cmykKText);
        if (c == null || m == null || y == null || k == null) {
            return;
        }

        float c1 = normalizePercent(c);
        float m1 = normalizePercent(m);
        float y1 = normalizePercent(y);
        float k1 = normalizePercent(k);
        float r = (1f - c1) * (1f - k1);
        float g = (1f - m1) * (1f - k1);
        float b = (1f - y1) * (1f - k1);
        applyColor(new ShatterColor(clamp01(r), clamp01(g), clamp01(b), alpha));
    }

    private void applyHsvFields() {
        if (syncingFields) {
            return;
        }

        Float h = parseDecimal(hsvHText);
        Float s = parseDecimal(hsvSText);
        Float v = parseDecimal(hsvVText);
        if (h == null || s == null || v == null) {
            return;
        }

        applyColor(ShatterColor.fromHSV(normalizeHue(h), normalizePercent(s), normalizePercent(v), alpha));
    }

    private void applyHslFields() {
        if (syncingFields) {
            return;
        }

        Float h = parseDecimal(hslHText);
        Float s = parseDecimal(hslSText);
        Float l = parseDecimal(hslLText);
        if (h == null || s == null || l == null) {
            return;
        }

        applyColor(fromHsl(normalizeHue(h), normalizePercent(s), normalizePercent(l), alpha));
    }

    private void toggleExpanded() {
        setExpanded(!expanded);
    }

    private void setExpanded(boolean expanded) {
        if (this.expanded == expanded) {
            return;
        }

        this.expanded = expanded;
        if (expanded) {
            moveToTheTop();
        } else {
            dragTarget = DragTarget.NONE;
        }

        repositionElements();
        super.requestRelayout();
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        rowHexWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        togglePopupButton.render(guiGraphics, mouseX, mouseY, partialTick);
        renderToggleColorSwatch(guiGraphics);

        if (!expanded) {
            return;
        }

        PopupLayout layout = buildPopupLayout();
        Rect panelRect = toGlobalRect(layout.panelLocalX(), layout.panelLocalY(), layout.panelWidth(), layout.panelHeight());
        guiGraphics.fill(panelRect.x(), panelRect.y(), panelRect.right(), panelRect.bottom(), PANEL_COLOR);
        renderFrame(guiGraphics, panelRect);

        renderFieldLabel(guiGraphics, "HEX", layout.labelLocalX(), layout.hexFieldLocalY());
        renderFieldLabel(guiGraphics, "RGB", layout.labelLocalX(), layout.rgbFieldLocalY());
        renderFieldLabel(guiGraphics, "CMYK", layout.labelLocalX(), layout.cmykFieldLocalY());
        renderFieldLabel(guiGraphics, "HSV", layout.labelLocalX(), layout.hsvFieldLocalY());
        renderFieldLabel(guiGraphics, "HSL", layout.labelLocalX(), layout.hslFieldLocalY());

        popupHexWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        rgbRWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        rgbGWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        rgbBWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        cmykCWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        cmykMWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        cmykYWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        cmykKWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        hsvHWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        hsvSWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        hsvVWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        hslHWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        hslSWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        hslLWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        Rect svRect = toGlobalRect(layout.pickerLocalX(), layout.svLocalY(), layout.pickerWidth(), SV_H);
        Rect hueRect = toGlobalRect(layout.pickerLocalX(), layout.hueLocalY(), layout.pickerWidth(), SLIDER_H);
        Rect alphaRect = toGlobalRect(layout.pickerLocalX(), layout.alphaLocalY(), layout.pickerWidth(), SLIDER_H);

        renderSvArea(guiGraphics, svRect);
        renderFrame(guiGraphics, svRect);
        renderSvMarker(guiGraphics, svRect);

        renderHueSlider(guiGraphics, hueRect);
        renderFrame(guiGraphics, hueRect);
        renderSliderMarker(guiGraphics, hueRect, hue);

        renderAlphaSlider(guiGraphics, alphaRect);
        renderFrame(guiGraphics, alphaRect);
        renderSliderMarker(guiGraphics, alphaRect, alpha);
    }

    private void renderToggleColorSwatch(GuiGraphics guiGraphics) {
        int swatchSize = 9;
        int offsetY = togglePopupButton.isHovered() ? 1 : 0;
        int x = togglePopupButton.getX() + (togglePopupButton.getWidth() - swatchSize) / 2;
        int y = togglePopupButton.getY() + (togglePopupButton.getHeight() - swatchSize) / 2 - 1 + offsetY;
        Rect rect = new Rect(x, y, swatchSize, swatchSize);

        if (getCurrentColor().a() < 0.999f) {
            renderCheckerboard(guiGraphics, rect);
        }

        guiGraphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), getCurrentColor().getARGB());
        guiGraphics.fill(rect.x(), rect.y(), rect.right(), rect.y() + 1, 0xff101015);
        guiGraphics.fill(rect.x(), rect.bottom() - 1, rect.right(), rect.bottom(), 0xff101015);
        guiGraphics.fill(rect.x(), rect.y(), rect.x() + 1, rect.bottom(), 0xff101015);
        guiGraphics.fill(rect.right() - 1, rect.y(), rect.right(), rect.bottom(), 0xff101015);
    }

    private void renderFieldLabel(GuiGraphics guiGraphics, String label, int labelLocalX, int fieldLocalY) {
        int x = this.getX() + labelLocalX;
        int y = this.getY() + fieldLocalY + 4;
        guiGraphics.drawString(Minecraft.getInstance().font, label, x, y, LABEL_COLOR, true);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (expanded && event.button() == 0 && !isInsideRowOrPopup(event.x(), event.y())) {
            this.setFocused((GuiEventListener) null);
            setExpanded(false);
        }

        boolean childHandled = ContainerEventHandler.super.mouseClicked(event, isDoubleClick);
        if (childHandled) {
            return true;
        }

        if (this.isMouseOver(event.x(), event.y())) {
            this.setFocused((GuiEventListener) null);
        }

        if (event.button() != 0 || !expanded) {
            return super.mouseClicked(event, isDoubleClick);
        }

        DragTarget target = resolveDragTarget(event.x(), event.y());
        if (target != DragTarget.NONE) {
            dragTarget = target;
            updateFromMouse(event.x(), event.y());
            moveToTheTop();
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        if (isInsidePopup(event.x(), event.y())) {
            return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            dragTarget = DragTarget.NONE;
        }

        ContainerEventHandler.super.mouseReleased(event);
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (event.button() == 0 && dragTarget != DragTarget.NONE) {
            updateFromMouse(event.x(), event.y());
            return true;
        }

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
        List<GuiEventListener> children = new ArrayList<>();
        children.add(rowHexWidget);
        children.add(togglePopupButton);
        if (expanded) {
            children.add(popupHexWidget);
            children.add(rgbRWidget);
            children.add(rgbGWidget);
            children.add(rgbBWidget);
            children.add(cmykCWidget);
            children.add(cmykMWidget);
            children.add(cmykYWidget);
            children.add(cmykKWidget);
            children.add(hsvHWidget);
            children.add(hsvSWidget);
            children.add(hsvVWidget);
            children.add(hslHWidget);
            children.add(hslSWidget);
            children.add(hslLWidget);
        }
        return children;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        dragging = isDragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
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
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        if (rowHexWidget.isMouseOver(mouseX, mouseY) || togglePopupButton.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        if (!expanded) {
            return false;
        }

        if (popupHexWidget.isMouseOver(mouseX, mouseY)
                || rgbRWidget.isMouseOver(mouseX, mouseY)
                || rgbGWidget.isMouseOver(mouseX, mouseY)
                || rgbBWidget.isMouseOver(mouseX, mouseY)
                || cmykCWidget.isMouseOver(mouseX, mouseY)
                || cmykMWidget.isMouseOver(mouseX, mouseY)
                || cmykYWidget.isMouseOver(mouseX, mouseY)
                || cmykKWidget.isMouseOver(mouseX, mouseY)
                || hsvHWidget.isMouseOver(mouseX, mouseY)
                || hsvSWidget.isMouseOver(mouseX, mouseY)
                || hsvVWidget.isMouseOver(mouseX, mouseY)
                || hslHWidget.isMouseOver(mouseX, mouseY)
                || hslSWidget.isMouseOver(mouseX, mouseY)
                || hslLWidget.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        return isInsidePopup(mouseX, mouseY) || resolveDragTarget(mouseX, mouseY) != DragTarget.NONE;
    }

    private void updateFromMouse(double mouseX, double mouseY) {
        if (dragTarget == DragTarget.NONE) {
            return;
        }

        PopupLayout layout = buildPopupLayout();
        Rect svRect = toGlobalRect(layout.pickerLocalX(), layout.svLocalY(), layout.pickerWidth(), SV_H);
        Rect hueRect = toGlobalRect(layout.pickerLocalX(), layout.hueLocalY(), layout.pickerWidth(), SLIDER_H);
        Rect alphaRect = toGlobalRect(layout.pickerLocalX(), layout.alphaLocalY(), layout.pickerWidth(), SLIDER_H);

        switch (dragTarget) {
            case SV -> {
                saturation = positionToRatio(svRect, mouseX);
                value = 1f - positionToRatioVertical(svRect, mouseY);
            }
            case HUE -> hue = positionToRatio(hueRect, mouseX);
            case ALPHA -> alpha = positionToRatio(alphaRect, mouseX);
            default -> {
                return;
            }
        }

        applyColor(ShatterColor.fromHSV(hue, saturation, value, alpha));
    }

    private DragTarget resolveDragTarget(double mouseX, double mouseY) {
        if (!expanded) {
            return DragTarget.NONE;
        }

        PopupLayout layout = buildPopupLayout();
        Rect svRect = toGlobalRect(layout.pickerLocalX(), layout.svLocalY(), layout.pickerWidth(), SV_H);
        Rect hueRect = toGlobalRect(layout.pickerLocalX(), layout.hueLocalY(), layout.pickerWidth(), SLIDER_H);
        Rect alphaRect = toGlobalRect(layout.pickerLocalX(), layout.alphaLocalY(), layout.pickerWidth(), SLIDER_H);

        if (svRect.contains(mouseX, mouseY)) {
            return DragTarget.SV;
        }
        if (hueRect.contains(mouseX, mouseY)) {
            return DragTarget.HUE;
        }
        if (alphaRect.contains(mouseX, mouseY)) {
            return DragTarget.ALPHA;
        }

        return DragTarget.NONE;
    }

    private boolean isInsideRowOrPopup(double mouseX, double mouseY) {
        Rect row = toGlobalRect(0, 0, this.getWidth(), ROW_H);
        if (row.contains(mouseX, mouseY)) {
            return true;
        }

        return isInsidePopup(mouseX, mouseY);
    }

    private boolean isInsidePopup(double mouseX, double mouseY) {
        if (!expanded) {
            return false;
        }

        PopupLayout layout = buildPopupLayout();
        Rect popup = toGlobalRect(layout.panelLocalX(), layout.panelLocalY(), layout.panelWidth(), layout.panelHeight());
        return popup.contains(mouseX, mouseY);
    }

    private int getRowHexWidth() {
        int availableWidth = Math.max(24, this.getWidth() - BTN_W - ROW_GAP);
        return Math.min(COLLAPSED_HEX_WIDTH, availableWidth);
    }

    private PopupLayout buildPopupLayout() {
        int panelLocalY = ROW_H + DROP_GAP;
        int baseContentWidth = Math.max(1, this.getWidth() - PAD * 2);

        int pickerWidth = Math.clamp((int) Math.round(baseContentWidth * 0.42), PICKER_MIN, PICKER_MAX);
        int baseLeftWidth = baseContentWidth - pickerWidth - COL_GAP;
        if (baseLeftWidth < MIN_LEFT) {
            pickerWidth = Math.max(PICKER_MIN, baseContentWidth - COL_GAP - MIN_LEFT);
            baseLeftWidth = baseContentWidth - pickerWidth - COL_GAP;
        }

        baseLeftWidth = Math.max(LABEL_W + 30, baseLeftWidth);
        pickerWidth = Math.max(56, baseContentWidth - baseLeftWidth - COL_GAP);

        int leftWidth = Math.max(LABEL_W + 30, Math.round(baseLeftWidth * TEXT_COLUMN_EXPAND_FACTOR));
        int contentWidth = leftWidth + COL_GAP + pickerWidth;
        int panelWidth = contentWidth + PAD * 2;
        int panelLocalX = this.getWidth() - panelWidth;

        int labelLocalX = panelLocalX + PAD;
        int fieldLocalX = labelLocalX + LABEL_W;
        int fieldWidth = Math.max(24, leftWidth - LABEL_W);

        int rowStartLocalY = panelLocalY + PAD;
        int hexFieldLocalY = rowStartLocalY;
        int rgbFieldLocalY = hexFieldLocalY + FIELD_H + FIELD_GAP;
        int cmykFieldLocalY = rgbFieldLocalY + FIELD_H + FIELD_GAP;
        int hsvFieldLocalY = cmykFieldLocalY + FIELD_H + FIELD_GAP;
        int hslFieldLocalY = hsvFieldLocalY + FIELD_H + FIELD_GAP;
        int leftBottomLocalY = hslFieldLocalY + FIELD_H;

        int pickerLocalX = labelLocalX + leftWidth + COL_GAP;
        int svLocalY = rowStartLocalY;
        int hueLocalY = svLocalY + SV_H + SLIDER_GAP;
        int alphaLocalY = hueLocalY + SLIDER_H + SLIDER_GAP;
        int pickerBottomLocalY = alphaLocalY + SLIDER_H;

        int panelHeight = Math.max(leftBottomLocalY, pickerBottomLocalY) - panelLocalY + PAD;
        int totalHeight = panelLocalY + panelHeight;

        return new PopupLayout(
                panelLocalX,
                panelLocalY,
                panelWidth,
                panelHeight,
                labelLocalX,
                fieldLocalX,
                fieldWidth,
                hexFieldLocalY,
                rgbFieldLocalY,
                cmykFieldLocalY,
                hsvFieldLocalY,
                hslFieldLocalY,
                pickerLocalX,
                svLocalY,
                pickerWidth,
                hueLocalY,
                alphaLocalY,
                totalHeight
        );
    }

    private Rect toGlobalRect(int localX, int localY, int width, int height) {
        return new Rect(this.getX() + localX, this.getY() + localY, width, height);
    }

    private void renderSvArea(GuiGraphics guiGraphics, Rect rect) {
        int right = rect.right();
        for (int x = rect.x(); x < right; x++) {
            float t = rect.width() <= 1 ? 0f : (x - rect.x()) / (float) (rect.width() - 1);
            int topColor = ShatterColor.fromHSV(hue, clamp01(t), 1f, 1f).getARGB();
            guiGraphics.fillGradient(x, rect.y(), x + 1, rect.bottom(), topColor, 0xff000000);
        }
    }

    private void renderHueSlider(GuiGraphics guiGraphics, Rect rect) {
        int right = rect.right();
        for (int x = rect.x(); x < right; x++) {
            float t = rect.width() <= 1 ? 0f : (x - rect.x()) / (float) (rect.width() - 1);
            guiGraphics.fill(x, rect.y(), x + 1, rect.bottom(), ShatterColor.fromHSV(clamp01(t), 1f, 1f, 1f).getARGB());
        }
    }

    private void renderAlphaSlider(GuiGraphics guiGraphics, Rect rect) {
        renderCheckerboard(guiGraphics, rect);
        ShatterColor opaque = ShatterColor.fromHSV(hue, saturation, value, 1f);
        int right = rect.right();
        for (int x = rect.x(); x < right; x++) {
            float t = rect.width() <= 1 ? 0f : (x - rect.x()) / (float) (rect.width() - 1);
            guiGraphics.fill(x, rect.y(), x + 1, rect.bottom(), toArgb(opaque.r(), opaque.g(), opaque.b(), clamp01(t)));
        }
    }

    private void renderCheckerboard(GuiGraphics guiGraphics, Rect rect) {
        int tileSize = 3;
        for (int y = rect.y(); y < rect.bottom(); y += tileSize) {
            for (int x = rect.x(); x < rect.right(); x += tileSize) {
                boolean light = ((x - rect.x()) / tileSize + (y - rect.y()) / tileSize) % 2 == 0;
                int color = light ? CHECKER_LIGHT : CHECKER_DARK;
                guiGraphics.fill(x, y, Math.min(x + tileSize, rect.right()), Math.min(y + tileSize, rect.bottom()), color);
            }
        }
    }

    private void renderSvMarker(GuiGraphics guiGraphics, Rect rect) {
        int markerX = rect.x() + Math.round(saturation * Math.max(0, rect.width() - 1));
        int markerY = rect.y() + Math.round((1f - value) * Math.max(0, rect.height() - 1));
        renderCircleMarker(guiGraphics, markerX, markerY, 4);
    }

    private void renderSliderMarker(GuiGraphics guiGraphics, Rect rect, float progress) {
        int markerX = rect.x() + Math.round(clamp01(progress) * Math.max(0, rect.width() - 1));
        int markerY = rect.y() + rect.height() / 2;
        renderCircleMarker(guiGraphics, markerX, markerY, 5);
    }

    private void renderCircleMarker(GuiGraphics guiGraphics, int centerX, int centerY, int radius) {
        int outerSq = radius * radius;
        int innerRadius = Math.max(1, radius - 1);
        int innerSq = innerRadius * innerRadius;

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int distSq = dx * dx + dy * dy;
                if (distSq > outerSq) {
                    continue;
                }

                int px = centerX + dx;
                int py = centerY + dy;
                int color = distSq >= innerSq ? 0xff000000 : 0xffffffff;
                guiGraphics.fill(px, py, px + 1, py + 1, color);
            }
        }
    }

    private void renderFrame(GuiGraphics guiGraphics, Rect rect) {
        UIElements.FRAME.render(guiGraphics, RenderPipelines.GUI_TEXTURED, rect.x() - 1, rect.y() - 1, rect.width() + 2, rect.height() + 2);
        guiGraphics.fill(rect.x(), rect.y(), rect.right(), rect.y() + 1, PANEL_BORDER);
        guiGraphics.fill(rect.x(), rect.bottom() - 1, rect.right(), rect.bottom(), PANEL_BORDER);
    }

    private ShatterColor getCurrentColor() {
        ShatterColor value = this.getValue();
        return value == null ? ShatterColor.WHITE : value;
    }

    private static String formatHex(ShatterColor color) {
        int argb = color.getARGB();
        if (color.a() >= 0.999f) {
            return String.format(Locale.ROOT, "#%06X", argb & 0x00FFFFFF);
        }

        return String.format(Locale.ROOT, "#%08X", argb);
    }

    private static int[] rgbComponents(ShatterColor color) {
        return new int[]{
                clampToByte(color.r()),
                clampToByte(color.g()),
                clampToByte(color.b())
        };
    }

    private static int[] cmykComponents(ShatterColor color) {
        float r = clamp01(color.r());
        float g = clamp01(color.g());
        float b = clamp01(color.b());

        float k = 1f - Math.max(r, Math.max(g, b));
        float c;
        float m;
        float y;
        if (k >= 0.999f) {
            c = 0f;
            m = 0f;
            y = 0f;
        } else {
            float inv = 1f - k;
            c = (1f - r - k) / inv;
            m = (1f - g - k) / inv;
            y = (1f - b - k) / inv;
        }

        return new int[]{
                Math.round(clamp01(c) * 100f),
                Math.round(clamp01(m) * 100f),
                Math.round(clamp01(y) * 100f),
                Math.round(clamp01(k) * 100f)
        };
    }

    private int[] hsvComponents() {
        return new int[]{
                Math.round(hue * 360f),
                Math.round(saturation * 100f),
                Math.round(value * 100f)
        };
    }

    private static int[] hslComponents(ShatterColor color) {
        float[] hsl = toHsl(color);
        return new int[]{
                Math.round(hsl[0] * 360f),
                Math.round(hsl[1] * 100f),
                Math.round(hsl[2] * 100f)
        };
    }

    private static float[] toHsl(ShatterColor color) {
        float r = clamp01(color.r());
        float g = clamp01(color.g());
        float b = clamp01(color.b());
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        float l = (max + min) * 0.5f;
        float h = 0f;
        float s = 0f;

        if (delta != 0f) {
            s = delta / (1f - Math.abs(2f * l - 1f));
            if (max == r) {
                h = ((g - b) / delta) % 6f;
            } else if (max == g) {
                h = ((b - r) / delta) + 2f;
            } else {
                h = ((r - g) / delta) + 4f;
            }

            h /= 6f;
            if (h < 0f) {
                h += 1f;
            }
        }

        return new float[]{clamp01(h), clamp01(s), clamp01(l)};
    }

    private static @Nullable ShatterColor parseHex(String text) {
        String normalized = text.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        } else if (normalized.startsWith("0x") || normalized.startsWith("0X")) {
            normalized = normalized.substring(2);
        }

        if (normalized.length() != 6 && normalized.length() != 8) {
            return null;
        }

        if (!normalized.matches("(?i)[0-9a-f]+")) {
            return null;
        }

        int value = (int) Long.parseLong(normalized, 16);
        if (normalized.length() == 6) {
            value |= 0xFF000000;
        }

        return new ShatterColor(value);
    }

    private static @Nullable Float parseDecimal(String text) {
        String normalized = text.trim();
        if (normalized.isEmpty() || normalized.equals("-") || normalized.equals("+") || normalized.equals(".") || normalized.equals("-.") || normalized.equals("+.")) {
            return null;
        }

        try {
            return Float.parseFloat(normalized);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static ShatterColor fromHsl(float h, float s, float l, float a) {
        h = clamp01(h);
        s = clamp01(s);
        l = clamp01(l);

        float c = (1f - Math.abs(2f * l - 1f)) * s;
        float hPrime = h * 6f;
        float x = c * (1f - Math.abs(hPrime % 2f - 1f));

        float r1;
        float g1;
        float b1;
        if (hPrime < 1f) {
            r1 = c; g1 = x; b1 = 0f;
        } else if (hPrime < 2f) {
            r1 = x; g1 = c; b1 = 0f;
        } else if (hPrime < 3f) {
            r1 = 0f; g1 = c; b1 = x;
        } else if (hPrime < 4f) {
            r1 = 0f; g1 = x; b1 = c;
        } else if (hPrime < 5f) {
            r1 = x; g1 = 0f; b1 = c;
        } else {
            r1 = c; g1 = 0f; b1 = x;
        }

        float m = l - c * 0.5f;
        return new ShatterColor(clamp01(r1 + m), clamp01(g1 + m), clamp01(b1 + m), clamp01(a));
    }

    private static boolean isHexLikeInput(String value) {
        return value.length() <= 16 && value.matches("(?i)[0-9a-fx#\\s]*");
    }

    private static boolean isNumericLikeInput(String value) {
        return value.length() <= 12 && value.matches("[-+]?\\d*(\\.\\d*)?");
    }

    private static float normalizeRgb(double value) {
        float v = (float) value;
        if (Math.abs(v) > 1f) {
            v /= 255f;
        }
        return clamp01(v);
    }

    private static float normalizePercent(double value) {
        float v = (float) value;
        if (Math.abs(v) > 1f) {
            v /= 100f;
        }
        return clamp01(v);
    }

    private static float normalizeHue(double value) {
        float h = (float) value;
        if (Math.abs(h) > 1f) {
            h /= 360f;
        }
        h %= 1f;
        if (h < 0f) {
            h += 1f;
        }
        return h;
    }

    private static int toArgb(float red, float green, float blue, float alpha) {
        int a = clampToByte(alpha);
        int r = clampToByte(red);
        int g = clampToByte(green);
        int b = clampToByte(blue);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clampToByte(float value) {
        return Math.round(clamp01(value) * 255f);
    }

    private static float clamp01(float value) {
        return Math.max(0f, Math.min(1f, value));
    }

    private static float positionToRatio(Rect rect, double mouseX) {
        if (rect.width() <= 1) {
            return 0f;
        }

        double clamped = Math.clamp(mouseX, rect.x(), rect.right() - 1);
        return (float) ((clamped - rect.x()) / (double) (rect.width() - 1));
    }

    private static float positionToRatioVertical(Rect rect, double mouseY) {
        if (rect.height() <= 1) {
            return 0f;
        }

        double clamped = Math.clamp(mouseY, rect.y(), rect.bottom() - 1);
        return (float) ((clamped - rect.y()) / (double) (rect.height() - 1));
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

    @Override
    public void playDownSound(SoundManager handler) {
        super.playDownSound(handler);
    }

    private enum DragTarget {
        NONE,
        SV,
        HUE,
        ALPHA
    }

    private record Rect(int x, int y, int width, int height) {
        boolean contains(double px, double py) {
            return px >= this.x && px < this.right() && py >= this.y && py < this.bottom();
        }

        int right() {
            return this.x + this.width;
        }

        int bottom() {
            return this.y + this.height;
        }
    }

    private record PopupLayout(
            int panelLocalX,
            int panelLocalY,
            int panelWidth,
            int panelHeight,
            int labelLocalX,
            int fieldLocalX,
            int fieldWidth,
            int hexFieldLocalY,
            int rgbFieldLocalY,
            int cmykFieldLocalY,
            int hsvFieldLocalY,
            int hslFieldLocalY,
            int pickerLocalX,
            int svLocalY,
            int pickerWidth,
            int hueLocalY,
            int alphaLocalY,
            int totalHeight
    ) {}
}
