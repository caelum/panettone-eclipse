package br.com.caelum.panettone.eclipse.builder;

public class Filenames {

	public static boolean isTone(String name) {
		return name.endsWith(".tone") || name.contains(".tone.");
	}

	public static boolean isCotti(String name) {
		return name.endsWith(".properties") || name.startsWith("messages.");
	}

}
