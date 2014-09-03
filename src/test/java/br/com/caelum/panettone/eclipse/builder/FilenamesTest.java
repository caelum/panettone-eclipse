package br.com.caelum.panettone.eclipse.builder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FilenamesTest {
	
	@Test
	public void shouldAcceptToneExtensions() {
		assertTrue(Filenames.isTone("hi.tone.html"));
		assertTrue(Filenames.isTone("hi.tone"));
		assertFalse(Filenames.isTone("tone.html"));
	}

}
