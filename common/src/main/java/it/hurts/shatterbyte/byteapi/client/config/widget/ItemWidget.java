package it.hurts.shatterbyte.byteapi.client.config.widget;

import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemWidget extends IdentifierWidget {
    private static final int ICON_SIZE = 16;
    private static final int ICON_GAP = 4;

    public ItemWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, Item defaultValue, Supplier<Item> getter, Consumer<Item> setter) {
        super(config, type, annotations, parent, toItemId(defaultValue), () -> toItemId(getter.get()), identifier -> setter.accept(resolveItem(identifier)));
    }

    @Override
    protected String getPlaceholder() {
        return "minecraft:stone";
    }

    @Override
    protected int getLeadingWidth() {
        return ICON_SIZE + ICON_GAP;
    }

    @Override
    protected int getWidgetHeight() {
        return ICON_SIZE;
    }

    @Override
    protected void renderLeading(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        ItemStack stack = getPreviewStack();

        if (stack.isEmpty()) {
            return;
        }

        guiGraphics.fakeItem(stack, this.getX(), this.getY());
    }

    private ItemStack getPreviewStack() {
        String textValue = getTextValue();
        if (!textValue.isBlank()) {
            Item parsedItem = parseItem(textValue);
            if (parsedItem == null) {
                return ItemStack.EMPTY;
            }

            if (!parsedItem.builtInRegistryHolder().areComponentsBound()) {
                return ItemStack.EMPTY;
            }

            return parsedItem.getDefaultInstance();
        }

        Item currentValue = resolveItem(normalizeIdentifier(this.getValue(), getDefaultValue()));
        if (currentValue == Items.AIR) {
            return ItemStack.EMPTY;
        }

        if (!currentValue.builtInRegistryHolder().areComponentsBound()) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(currentValue);
    }

    @Override
    protected @Nullable Identifier parseIdentifier(@Nullable String value) {
        Item item = parseItem(value);
        return item == null ? null : BuiltInRegistries.ITEM.getKey(item);
    }

    @Override
    protected Identifier normalizeIdentifier(@Nullable Identifier value, @Nullable Identifier fallback) {
        Item item = resolveItem(value);
        if (item != Items.AIR || value != null) {
            return BuiltInRegistries.ITEM.getKey(item);
        }

        return toItemId(resolveItem(fallback));
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

    private static Item resolveItem(@Nullable Identifier id) {
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
            return Items.AIR;
        }

        return BuiltInRegistries.ITEM.getValue(id);
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

    private static Identifier toItemId(@Nullable Item item) {
        return BuiltInRegistries.ITEM.getKey(normalizeItem(item, Items.AIR));
    }
}
