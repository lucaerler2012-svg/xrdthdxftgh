package dev.kebab.biggerbundles;

import java.util.function.Function;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.BundleContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiggerBundlesMod implements ModInitializer {
	public static final String MOD_ID = "biggerbundles";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Item LARGE_BUNDLE;
	public static Item SUPER_LARGE_BUNDLE;

	@Override
	public void onInitialize() {
		LARGE_BUNDLE = register("large_bundle", BundleItem::new,
				new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
						.component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
		SUPER_LARGE_BUNDLE = register("super_large_bundle", BundleItem::new,
				new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
						.component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));

		LOGGER.info("Bigger Bundles geladen.");
	}

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
	}
}
