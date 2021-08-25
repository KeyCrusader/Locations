package keyboardcrusader.locations.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;


public class OverlayScreen extends AbstractGui {
    private String message = null;
    private int tick = 0;
    private int priority = 0;

    public void showMessage(String message, int priority) {
        if (priority >= this.priority) {
            this.tick = 100;
            this.message = message;
        }
    }

    public void doTick() {
        if (this.tick > 0) this.tick--;
        if (this.tick == 0 && this.message != null) {
            this.message = null;
            this.priority = 0;
        }
    }

    private boolean doRender() {
        return this.message != null && this.tick > 0;
    }

    public void render(MatrixStack matrixStack) {
        if (!doRender()) return;

        Minecraft mc = Minecraft.getInstance();
        FontRenderer fontRenderer = mc.fontRenderer;

        float scale = 2.5F;
        RenderSystem.scaled(scale, scale, scale);
        int scaledWidth = mc.getMainWindow().getScaledWidth();
        float x = ((scaledWidth/2) - ((fontRenderer.getStringWidth(this.message)/2)*scale)) / scale;
        fontRenderer.drawStringWithShadow(matrixStack, this.message, x, 15, 16777215);
        RenderSystem.scaled(1/scale, 1/scale, 1/scale);
    }
}
