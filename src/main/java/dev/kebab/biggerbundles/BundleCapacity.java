package dev.kebab.biggerbundles;

import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;

/**
 * Kapazitaet der Bundles. Normales Bundle = 1 (Vanilla), Large = 3, Super Large = 9.
 *
 * Waehrend ein Bundle angeklickt wird (BundleItem.overrideStackedOnOther / overrideOtherStackedOnMe),
 * merken wir uns die Kapazitaet des angeklickten Bundles in einem ThreadLocal. Die Mutable-Instanz,
 * die dabei erzeugt wird, liest den Wert beim Erzeugen aus.
 */
public final class BundleCapacity {
	public static final Fraction ONE = Fraction.ONE;
	public static final Fraction THREE = Fraction.getFraction(3, 1);
	public static final Fraction NINE = Fraction.getFraction(9, 1);

	public static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);
	public static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);

	private static final ThreadLocal<Fraction> CURRENT = new ThreadLocal<>();

	private BundleCapacity() {
	}

	public static Fraction forStack(ItemStack stack) {
		Item item = stack.getItem();
		if (item == BiggerBundlesMod.SUPER_LARGE_BUNDLE) {
			return NINE;
		}
		if (item == BiggerBundlesMod.LARGE_BUNDLE) {
			return THREE;
		}
		return ONE;
	}

	public static void begin(ItemStack bundle) {
		CURRENT.set(forStack(bundle));
	}

	public static void end() {
		CURRENT.remove();
	}

	public static Fraction current() {
		Fraction value = CURRENT.get();
		return value == null ? ONE : value;
	}
}
