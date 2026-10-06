package net.vindalarp;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import net.vindalarp.omni3d.api.BlockHighlight;
import net.vindalarp.omni3d.internal.Omni3dInit;
import net.vindalarp.omnibug.api.Omnibug;
import net.vindalarp.omnibug.api.OmnibugTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Omnia implements ModInitializer {
	public static final String MOD_ID = "omnia";

	@Override
	public void onInitialize() {
		Omni3dInit.init();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	//https://chatgpt.com/s/t_6aae95311e5c819180144dff7d0e3a2e
}
