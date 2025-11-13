package it.hurts.shatterbyte.shatterlib.client.particle;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import java.util.ArrayList;

public class UIParticle {
    public static final RenderPipeline ADDITIVE_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withBlend(BlendFunction.LIGHTNING)
            .withColorWrite(true)
            .withLocation(ResourceLocation.fromNamespaceAndPath(ShatterLib.MOD_ID, "additive"))
            .build();

    @Data
    public static class Texture2D {
        private ResourceLocation rl;
        private float width, height;
        private float texOffX, texOffY;
        private float texWidth, texHeight;

        public Texture2D(ResourceLocation texture, int texOffX, int texOffY, int texWidth, int texHeight, int width, int height) {
            this.rl = texture;
            this.texOffX = texOffX;
            this.texOffY = texOffY;
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            this.width = width;
            this.height = height;
        }

        public Texture2D(ResourceLocation texture, int width, int height) {
            this(texture, 0, 0, width, height, width, height);
        }
    }

    public enum Layer {
        GUI, SCREEN;
    }

    @Getter private final Texture2D texture;
    @Getter private final float maxSpeed;
    @Getter private final int lifetime;
    @Getter private final Layer layer;

    @Getter @Setter private Screen screen;
    @Getter private final Transform transform;
    @Getter private Vector2f direction = new Vector2f(0, 1);
    @Getter @Setter private float rollVelocity;
    @Getter @Setter private float speed;
    @Getter @Setter private int time;
    @Getter @Setter private float zOffset;
    @Getter private ShatterColor[] colors = new ShatterColor[]{ShatterColor.WHITE};
    @Getter @Setter private RenderPipeline renderPipeline = RenderPipelines.GUI_TEXTURED;
    private Matrix3x2f matrix = new Matrix3x2f();
//    @Getter @Setter private boolean resizeWithLifetime;

    public UIParticle(Texture2D texture, float maxSpeed, int lifetime, float xStart, float yStart, Layer layer, float zOffset) {
        this.texture = texture;
        this.maxSpeed = maxSpeed;
        this.lifetime = lifetime;
        this.speed = maxSpeed;
        this.layer = layer;
        this.zOffset = zOffset;

        this.transform = new Transform(new Vector2f(xStart, yStart), 0, new Vector2f(1f, 1f));
    }

    public void setMatrix(Matrix3x2f matrix) {
        this.matrix = new Matrix3x2f(matrix);
    }

    public Matrix3x2f getMatrix() {
        return new Matrix3x2f(matrix);
    }

    public void setColors(ShatterColor... colors) {
        this.colors = colors;
    }

    public void setDirection(float x, float y) {
        this.direction = new Vector2f(x, y).normalize();
    }

    public void setDirection(Vector2f direction) {
        this.direction = new Vector2f(direction).normalize();
    }

    public boolean isExpired() {
        return this.time >= this.lifetime;
    }

    public boolean isScreen() {
        return this.layer == Layer.SCREEN;
    }

    public boolean isGui() {
        return this.layer == Layer.GUI;
    }

    public void tick() {
        if (this.isExpired()) return;
        this.transform.updateOldValues();

        this.time = Mth.clamp(this.time + 1, 0, lifetime);

        if (Mth.abs(this.speed) > 0.01f) {
            this.transform.getPosition().add(new Vector2f(direction).mul(speed));
        }

        if (Mth.abs(this.rollVelocity) > 0.01f) {
            this.transform.setRoll(this.transform.getRoll() + rollVelocity);
        }
    }

    public void render(GuiGraphics guiGraphics, float partialTicks) {
        if (this.isExpired()) return;

        Texture2D tex = getTexture();

        Matrix3x2f matrix = this.getMatrix();
        this.transformPose(matrix, partialTicks);

        ShatterColor color = getColor(partialTicks);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().mul(matrix);
        RenderUtils.renderTextureFromCenter(
                this.getRenderPipeline(),
                tex.rl,
                guiGraphics,
                0,
                0,
                tex.texOffX,
                tex.texOffY,
                (int) tex.texWidth,
                (int) tex.texHeight,
                tex.width,
                tex.height,
                1,
                color.getARGB()
        );
        guiGraphics.pose().popMatrix();
    }

    private void transformPose(Matrix3x2f pose, float partialTicks) {
        Vector2f interpPos = transform.getInterpolatedPosition(partialTicks);
        float interpRot = transform.getInterpolatedRoll(partialTicks);
        Vector2f interpSize = transform.getInterpolatedSize(partialTicks);

        pose.translate(interpPos.x, interpPos.y);
        pose.scale(interpSize.x, interpSize.y);
        pose.rotate((float) Math.toRadians(interpRot));
    }

    public float getTimeRatio(float partialTicks) {
        return (this.time + partialTicks) / this.lifetime;
    }

    public ShatterColor getColor(float partialTicks) {
        return ShatterColor.lerp(this.getTimeRatio(partialTicks), colors);
    }

    public void instantiate() {
        if (this.isScreen()) {
            if (this.screen == null) return;

            ParticleSystem.SCREEN_PARTICLES.putIfAbsent(screen, new ArrayList<>());
            ParticleSystem.SCREEN_PARTICLES.get(screen).add(this);
            return;
        }

        ParticleSystem.GUI_PARTICLES.add(this);
    }
}
