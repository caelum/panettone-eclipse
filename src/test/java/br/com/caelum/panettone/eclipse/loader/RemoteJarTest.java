package br.com.caelum.panettone.eclipse.loader;

import static org.junit.Assert.*;

import org.junit.Test;

public class RemoteJarTest {
	
	@Test
	public void should_extract_without_version() {
		RemoteJar jar = new RemoteJar("http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-panettone/4.1.0/vraptor-panettone-4.1.0.jar");
		assertEquals("vraptor-panettone-", jar.getNameWithoutVersion());
		assertEquals("vraptor-panettone-4.1.0.jar", jar.getFullFilename());
	}

	@Test
	public void should_extract_without_version_and_snapshot() {
		RemoteJar jar = new RemoteJar("http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-panettone/4.1.0/vraptor-panettone-4.1.0-SNAPSHOT.jar");
		assertEquals("vraptor-panettone-", jar.getNameWithoutVersion());
		assertEquals("vraptor-panettone-4.1.0-SNAPSHOT.jar", jar.getFullFilename());
	}

	@Test
	public void should_extract_without_version_and_rc_and_snapshot() {
		RemoteJar jar = new RemoteJar("http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-panettone/4.1.0/vraptor-panettone-4.1.0-RC3-SNAPSHOT.jar");
		assertEquals("vraptor-panettone-", jar.getNameWithoutVersion());
		assertEquals("vraptor-panettone-4.1.0-RC3-SNAPSHOT.jar", jar.getFullFilename());
	}

}
