package com.yourname.nbtstand.screen;

import com.yourname.nbtstand.util.NBTUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import java.util.Arrays;
import java.util.List;

public class NBTEditScreen extends Screen {
    private static final List<Item> BLOCKS = Arrays.asList(
        Items.BEDROCK, Items.DIAMOND_BLOCK, Items.NETHERITE_BLOCK,
        Items.DRAGON_EGG, Items.BEACON, Items.BARRIER
    );

    private TextFieldWidget nbtField;
    private TextFieldWidget countField;
    private Item selectedItem = Items.BEDROCK;

    public NBTEditScreen() {
        super(Text.literal("NBT Armor Stand Editor"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        
        nbtField = new TextFieldWidget(this.textRenderer, 
            centerX - 100, 45, 200, 20, Text.literal("NBT"));
        nbtField.setMaxLength(500);
        nbtField.setText("{CustomModelData:1}");
        this.addSelectableChild(nbtField);
        this.setInitialFocus(nbtField);

        countField = new TextFieldWidget(this.textRenderer, 
            centerX - 100, 80, 200, 20, Text.literal("Count"));
        countField.setText("1");
        this.addSelectableChild(countField);

        CyclingButtonWidget<Item> itemSelector = CyclingButtonWidget
            .<Item>builder(item -> Text.literal(item.getName().getString()))
            .values(BLOCKS)
            .initially(selectedItem)
            .build(centerX - 100, 115, 200, 20, 
                Text.literal("Block"), 
                (button, item) -> selectedItem = item);
        this.addDrawableChild(itemSelector);

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Apply"), 
            button -> apply())
            .dimensions(centerX - 100, 150, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Preset: Mini Block"), 
            button -> nbtField.setText(
                "{CustomModelData:1,Invisible:1b,Small:1b,NoBasePlate:1b}"))
            .dimensions(centerX - 100, 180, 200, 20).build());
    }

    private void apply() {
        try {
            int count = Integer.parseInt(countField.getText());
            if (count < 1) count = 1;
            if (count > 64) count = 64;
            NBTUtils.applyNBTToStand(selectedItem, nbtField.getText(), count);
            this.client.player.sendMessage(Text.literal("§aDone!"), true);
            this.close();
        } catch (Exception e) {
            this.client.player.sendMessage(
                Text.literal("§cError: " + e.getMessage()), true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(
            this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(
            this.textRenderer, "NBT Data:", this.width / 2 - 100, 32, 0xAAAAAA);
        context.drawTextWithShadow(
            this.textRenderer, "Count:", this.width / 2 - 100, 67, 0xAAAAAA);
        nbtField.render(context, mouseX, mouseY, delta);
        countField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
