package it.hurts.octostudios.octolib.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.octostudios.octolib.util.RenderUtils;
import it.hurts.octostudios.octolib.util.VectorUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import oshi.util.tuples.Pair;

import java.util.ArrayList;

public class UIParticle {
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
            this.rl = texture;
            this.width = width;
            this.height = height;
            this.texWidth = width;
            this.texHeight = height;
        }
    }

    public enum Layer {
        GUI,
        SCREEN;
    }

    @Getter
    private final Texture2D texture;

    @Getter
    private final float maxSpeed;
    @Getter
    private final double lifetime;
    @Getter
    private final Vector2f startPos;

    public Vector2f position;
    public float zOffset;
    public Vector2f direction;
    public Vector2f gravityDirection;
    public float speed;
    public float gravity;
    private float gravityAccel;
    public float friction;
    public float size;
    public float angularVelocity;
    public double time;
    public OctoColor startColor;
    public OctoColor endColor;
    public Pair<Integer, Integer> blendFunc;
    public boolean enableBlend;
    public boolean resizeWithLifetime;

    @Getter
    private Layer layer;
    @Getter
    @Setter
    private Screen screen;

    public UIParticle(Texture2D texture, float maxSpeed, double lifetimeInSeconds, float xStart, float yStart, Layer layer, float zOffset) {
        this.texture = texture;
        this.maxSpeed = maxSpeed;
        this.lifetime = lifetimeInSeconds;
        this.speed = maxSpeed;
        this.startPos = new Vector2f(xStart, yStart);
        this.position = startPos;
        this.size = 1f;
        this.friction = 0f;
        this.startColor = OctoColor.WHITE;
        this.endColor = OctoColor.ZERO;
        this.direction = new Vector2f(0, 1);
        this.gravityDirection = new Vector2f(0, 1);
        this.angularVelocity = 0f;
        this.resizeWithLifetime = true;
        this.zOffset = zOffset;

        this.enableBlend = true;
        this.blendFunc = new Pair<>(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        this.layer = layer;
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

    public void tick(double dt) {
        if (this.isExpired()) {
            return;
        }

        this.time = Mth.clamp(this.time + dt, 0, lifetime);

        if (angularVelocity != 0) {
            this.direction = VectorUtils.rotate(this.direction.normalize(), (float) (angularVelocity * dt));
        } else {
            this.direction.normalize();
        }

        this.speed = (float) Mth.clamp(this.speed * (1 - friction * dt), 0, maxSpeed);
        this.gravityAccel += (float) (gravity * dt);

        this.position.add(direction.mul((float) (speed * dt)));
        if (gravityAccel != 0) {
            this.position.add(gravityDirection.x * gravityAccel, gravityDirection.y * gravityAccel);
        }
    }

    private void transformPose(PoseStack pose) {

    }

    private double getTimeRatio() {
        return this.time / this.lifetime;
    }

    private OctoColor getColor() {
        return startColor.lerp(endColor, this.getTimeRatio());
    }

    public void render(GuiGraphics guiGraphics) {
        if (this.isExpired()) {
            return;
        }

        Texture2D tex = getTexture();
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        this.transformPose(pose);

        float lifePercentage = (float) this.getTimeRatio();

        OctoColor color = this.getColor();

        RenderSystem.setShaderColor(color.r(), color.g(), color.b(), color.a());
        RenderSystem.setShaderTexture(0, tex.rl);

        RenderSystem.enableBlend();
        if (enableBlend) {
            RenderSystem.blendFunc(blendFunc.getA(), blendFunc.getB());
        }

        RenderUtils.renderTextureFromCenter(pose, position.x, position.y, tex.texOffX, tex.texOffY,
                tex.texWidth,
                tex.texHeight, tex.width, tex.height, size * (resizeWithLifetime ? (1 - lifePercentage) : 1), zOffset);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        pose.popPose();
    }

    public void instantiate() {
        if (this.isScreen()) {
            if (this.screen == null) {
                return;
            }
            ParticleSystem.SCREEN_PARTICLES.putIfAbsent(screen, new ArrayList<>());
            ParticleSystem.SCREEN_PARTICLES.get(screen).add(this);
            return;
        }
        ParticleSystem.GUI_PARTICLES.add(this);
    }
}