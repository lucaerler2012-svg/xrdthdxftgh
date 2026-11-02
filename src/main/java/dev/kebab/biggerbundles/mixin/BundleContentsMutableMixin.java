package dev.kebab.biggerbundles.mixin;

import dev.kebab.biggerbundles.BundleCapacity;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ersetzt in BundleContents.Mutable die feste Kapazitaet 1 (Fraction.ONE) durch die Kapazitaet
 * des gerade bedienten Bundles (1, 3 oder 9).
 */
@Mixin(BundleContents.Mutable.class)
public abstract class BundleContentsMutableMixin {

	@Shadow
	private Fraction weight;

	@Unique
	private Fraction biggerbundles$capacity = Fraction.ONE;

	@Inject(method = "<init>(Ljava/util/List;Lorg/apache/commons/lang3/math/Fraction;I)V", at = @At("RETURN"))
	private void biggerbundles$readCapacity(List<ItemStack> items, Fraction w, int selectedItem, CallbackInfo ci) {
		this.biggerbundles$capacity = BundleCapacity.current();
	}

	/** Vanilla: Fraction.ONE.subtract(weight).divideBy(itemWeight) */
	@Inject(method = "getMaxAmountToAdd", at = @At("HEAD"), cancellable = true)
	private void biggerbundles$maxAmount(Fraction itemWeight, CallbackInfoReturnable<Integer> cir) {
		Fraction remaining = this.biggerbundles$capacity.subtract(this.weight);
		cir.setReturnValue(Math.max(remaining.divideBy(itemWeight).intValue(), 0));
	}

	/** Vanilla: weight < ONE */
	@Inject(method = "canInsertNewSlots", at = @At("HEAD"), cancellable = true)
	private void biggerbundles$canInsertNewSlots(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.weight.compareTo(this.biggerbundles$capacity) < 0);
	}
}
