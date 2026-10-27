package pl.example.botmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class BotMenuScreen extends Screen {
    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP = 26;

    public BotMenuScreen() {
        super(Text.literal("Bot Manager"));
    }

    @Override
    protected void init() {
        int x = (width - BUTTON_WIDTH) / 2;
        int y = height / 2 - 55;

        addActionButton("Stwórz Bota", BotNetworking.CREATE, x, y);
        addActionButton("Każ botom iść za mną", BotNetworking.FOLLOW, x, y + GAP);
        addActionButton("Zatrzymaj boty", BotNetworking.STOP, x, y + GAP * 2);
        addActionButton("Usuń najbliższego bota", BotNetworking.REMOVE_NEAREST, x, y + GAP * 3);

        addDrawableChild(ButtonWidget.builder(Text.literal("Zamknij"), button -> close())
                .dimensions(x, y + GAP * 4, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    private void addActionButton(String label, int action, int x, int y) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label), button -> {
            if (client != null && client.player != null) {
                BotNetworking.sendAction(action);
            }
        }).dimensions(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 90, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
