package com.yourname.nbtstand.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import java.util.List;

public class NBTUtils {
    public static void applyNBTToStand(Item item, String nbtString, int count) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) return;

        ArmorStandEntity stand = findNearestArmorStand(client);
        if (stand == null) {
            client.player.sendMessage(
                Text.literal("§cNo armor stand nearby!"), false);
            return;
        }

        try {
            NbtCompound nbt = StringNbtReader.parse(nbtString);
            ItemStack stack = new ItemStack(item, count);
            stack.setNbt(nbt);
            stand.equipStack(EquipmentSlot.HEAD, stack);
            client.interactionManager.interactEntity(
                client.player, stand, Hand.MAIN_HAND);
            client.player.sendMessage(Text.literal("§aApplied!"), true);
        } catch (Exception e) {
            client.player.sendMessage(
                Text.literal("§cNBT Error: " + e.getMessage()), false);
        }
    }

    private static ArmorStandEntity findNearestArmorStand(MinecraftClient client) {
        if (client.player == null || client.world == null) return null;
        Box box = client.player.getBoundingBox().expand(5.0);
        List<ArmorStandEntity> stands = client.world.getEntitiesByClass(
            ArmorStandEntity.class, box, stand -> true);
        return stands.isEmpty() ? null : stands.get(0);
    }
}
