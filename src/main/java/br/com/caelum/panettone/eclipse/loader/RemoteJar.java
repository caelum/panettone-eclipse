package br.com.caelum.panettone.eclipse.loader;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteJar {

	private final String repositoryJar;

	public RemoteJar(String repositoryJar) {
		this.repositoryJar = repositoryJar;
	}

	public String getNameWithoutVersion() {
		String full = getFullFilename();
		String upToNumber = full.replaceAll("\\d.*", "");
		return upToNumber;
	}

	public String getFullFilename() {
		return repositoryJar.substring(repositoryJar.lastIndexOf("/") + 1);
	}

	public URL toURL() throws MalformedURLException {
		return new URL(repositoryJar);
	}

}
