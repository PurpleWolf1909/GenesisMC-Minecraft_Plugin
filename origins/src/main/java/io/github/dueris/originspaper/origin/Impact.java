package io.github.dueris.originspaper.origin;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public enum Impact {

	NONE(0, "none", ChatFormatting.GRAY, OriginsPaper.identifier("choose_origin/impact/none")),
	LOW(1, "low", ChatFormatting.GREEN, OriginsPaper.identifier("choose_origin/impact/low")),
	MEDIUM(2, "medium", ChatFormatting.YELLOW, OriginsPaper.identifier("choose_origin/impact/medium")),
	HIGH(3, "high", ChatFormatting.RED, OriginsPaper.identifier("choose_origin/impact/high"));

	private final int impactValue;
	private final String translationKey;
	private final ChatFormatting textStyle;
	private final ResourceLocation spriteId;

	Impact(int impactValue, String translationKey, ChatFormatting textStyle, ResourceLocation spriteId) {
		this.translationKey = "origins.gui.impact." + translationKey;
		this.impactValue = impactValue;
		this.textStyle = textStyle;
		this.spriteId = spriteId;
	}

	public static Impact getByValue(int impactValue) {
		return Impact.values()[impactValue];
	}

	public ResourceLocation getSpriteId() {
		return spriteId;
	}

	public int getImpactValue() {
		return impactValue;
	}

	public String getTranslationKey() {
		return translationKey;
	}

	public ChatFormatting getTextStyle() {
		return textStyle;
	}

	public MutableComponent getTextComponent() {
		return Component.translatable(getTranslationKey()).withStyle(getTextStyle());
	}
}
