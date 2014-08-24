package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.resources.IFile;

import br.com.caelum.panettone.eclipse.DynamicPanettone;

public class Tone {

	static boolean isTone(IFile file) {
		return isToneExtension(file) && onPath(file);
	}

	static boolean onPath(IFile file) {
		return file.getProjectRelativePath().toString()
				.startsWith(DynamicPanettone.constantValue(file.getProject(), "VIEW_INPUT"));
	}

	static boolean isToneExtension(IFile file) {
		return file.getName().endsWith(".tone")
				|| file.getName().contains(".tone.");
	}

}
