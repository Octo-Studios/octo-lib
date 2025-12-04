package it.hurts.shatterbyte.shatterlib.client.config;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public abstract sealed class UISprite permits UISprite.Single, UISprite.NineSlice {
    @Getter
    Atlas atlas;

    private UISprite(Atlas atlas) {
        this.atlas = atlas;
    }

    @Getter
    public static non-sealed class Single extends UISprite {
        int x;
        int y;
        int width;
        int height;

        public Single(Atlas atlas, int x, int y, int width, int height) {
            super(atlas);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void render(GuiGraphics guiGraphics, RenderPipeline pipeline, int x, int y, int width, int height) {
            guiGraphics.blit(pipeline, this.atlas.location, x, y, this.x, this.y, width, height, this.width, this.height, this.atlas.textureWidth, this.atlas.textureHeight);
        }
    }

    @Getter
    public static non-sealed class NineSlice extends UISprite {
        //Region[] regions;

        Region topLeft;
        Region top;
        Region topRight;

        Region left;
        Region center;
        Region right;

        Region bottomLeft;
        Region bottom;
        Region bottomRight;

        public NineSlice(Atlas atlas, Region topLeft, Region top, Region topRight, Region left, Region center, Region right, Region bottomLeft, Region bottom, Region bottomRight) {
            super(atlas);
            this.topLeft = topLeft;
            this.top = top;
            this.topRight = topRight;
            this.left = left;
            this.center = center;
            this.right = right;
            this.bottomLeft = bottomLeft;
            this.bottom = bottom;
            this.bottomRight = bottomRight;

            //regions = new Region[]{topLeft, top, topRight, left, center, right, bottomLeft, bottom, bottomRight};
        }

        @Override
        public void render(GuiGraphics guiGraphics, RenderPipeline pipeline, int x, int y, int width, int height) {
            // source sizes from regions
            int leftW = topLeft.width;
            int rightW = topRight.width;
            int topH = topLeft.height;
            int bottomH = bottomLeft.height;

            // amount left for center area (can be 0)
            int centerW = Math.max(0, width - leftW - rightW);
            int centerH = Math.max(0, height - topH - bottomH);

            // atlas texture dims
            int texW = this.atlas.textureWidth;
            int texH = this.atlas.textureHeight;

            // positions for destination
            int xLeft = x;
            int xCenter = x + leftW;
            int xRight = x + leftW + centerW;

            int yTop = y;
            int yCenter = y + topH;
            int yBottom = y + topH + centerH;

            // top row
            // top-left
            guiGraphics.blit(pipeline, this.atlas.location, xLeft, yTop,
                    topLeft.x, topLeft.y, topLeft.width, topLeft.height,
                    topLeft.width, topLeft.height, texW, texH);

            // top (stretched horizontally to centerW)
            if (centerW > 0) {
                guiGraphics.blit(pipeline, this.atlas.location, xCenter, yTop,
                        top.x, top.y, centerW, top.height,
                        top.width, top.height, texW, texH);
            }

            // top-right
            guiGraphics.blit(pipeline, this.atlas.location, xRight, yTop,
                    topRight.x, topRight.y, topRight.width, topRight.height,
                    topRight.width, topRight.height, texW, texH);

            // middle row
            // left (stretched vertically to centerH)
            if (centerH > 0) {
                guiGraphics.blit(pipeline, this.atlas.location, xLeft, yCenter,
                        left.x, left.y, left.width, centerH,
                        left.width, left.height, texW, texH);

                // center (stretched both axes)
                guiGraphics.blit(pipeline, this.atlas.location, xCenter, yCenter,
                        center.x, center.y, centerW, centerH,
                        center.width, center.height, texW, texH);

                // right (stretched vertically)
                guiGraphics.blit(pipeline, this.atlas.location, xRight, yCenter,
                        right.x, right.y, right.width, centerH,
                        right.width, right.height, texW, texH);
            }

            // bottom row
            // bottom-left
            guiGraphics.blit(pipeline, this.atlas.location, xLeft, yBottom,
                    bottomLeft.x, bottomLeft.y, bottomLeft.width, bottomLeft.height,
                    bottomLeft.width, bottomLeft.height, texW, texH);

            // bottom (stretched horizontally)
            if (centerW > 0) {
                guiGraphics.blit(pipeline, this.atlas.location, xCenter, yBottom,
                        bottom.x, bottom.y, centerW, bottom.height,
                        bottom.width, bottom.height, texW, texH);
            }

            // bottom-right
            guiGraphics.blit(pipeline, this.atlas.location, xRight, yBottom,
                    bottomRight.x, bottomRight.y, bottomRight.width, bottomRight.height,
                    bottomRight.width, bottomRight.height, texW, texH);
        }

        public static class Region {
            int x;
            int y;
            int width;
            int height;
            boolean shouldTile = false;

            public Region(int x, int y, int width, int height) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }

            public Region(int x, int y, int width, int height, boolean shouldTile) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.shouldTile = shouldTile;
            }
        }
    }

    public void render(GuiGraphics guiGraphics, RenderPipeline pipeline, int x, int y) {
        if (this instanceof UISprite.Single single) {
            this.render(guiGraphics, pipeline, x, y, single.getWidth(), single.getHeight());
            return;
        }

        throw new IllegalArgumentException("Tried to render a nine-slice sprite without specifying width and height.");
    }

    public abstract void render(GuiGraphics guiGraphics, RenderPipeline pipeline, int x, int y, int width, int height);
}