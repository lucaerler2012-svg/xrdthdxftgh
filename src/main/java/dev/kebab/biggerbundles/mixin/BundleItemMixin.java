package dev.kebab.biggerbundles.mixin;

import dev.kebab.biggerbundles.BundleCapacity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Merkt sich beim Anklicken eines Bundles dessen Kapazitaet und passt die Fuellstandsanzeige an.
 */
@Mixin(BundleItem.class)
public abstract class BundleItemMixin {

	// --- Kapazitaet beim Einsortieren / Herausnehmen -------------------------------------------

	@Inject(method = "overrideStackedOnOther", at = @At("HEAD"))
	private void biggerbundles$beginStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player,
			CallbackInfoReturnable<Boolean> cir) {
		BundleCapacity.begin(self);
	}

	@Inject(method = "overrideStackedOnOther", at = @At("RETURN"))
	private void biggerbundles$endStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player,
			CallbackInfoReturnable<Boolean> cir) {
		BundleCapacity.end();
	}

	@Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"))
	private void biggerbundles$beginOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction,
			Player player, SlotAccess carriedItem, CallbackInfoReturnable<Boolean> cir) {
		BundleCapacity.begin(self);
	}

	@Inject(method = "overrideOtherStackedOnMe", at = @At("RETURN"))
	private void biggerbundles$endOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction,
			Player player, SlotAccess carriedItem, CallbackInfoReturnable<Boolean> cir) {
		BundleCapacity.end();
	}

	// --- Fuellstandsbalken ---------------------------------------------------------------------

	@Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
	private void biggerbundles$barWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		Fraction capacity = BundleCapacity.forStack(stack);
		if (capacity.compareTo(Fraction.ONE) == 0) {
			return;
		}
		BundleContents contents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		Fraction weight = contents.weight().result().orElse(capacity);
		cir.setReturnValue(Math.min(1 + Mth.mulAndTruncate(weight.divideBy(capacity), 12), 13));
	}

	@Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
	private void biggerbundles$barColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		Fraction capacity = BundleCapacity.forStack(stack);
		if (capacity.compareTo(Fraction.ONE) == 0) {
			return;
		}
		BundleContents contents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		Fraction weight = contents.weight().result().orElse(capacity);
		cir.setReturnValue(weight.compareTo(capacity) >= 0 ? BundleCapacity.FULL_BAR_COLOR : BundleCapacity.BAR_COLOR);
	}
}
