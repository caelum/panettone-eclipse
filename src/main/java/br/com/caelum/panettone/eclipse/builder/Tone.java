package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.PanettoneProject;

public class Tone {

	static boolean isTone(IFile file) throws CoreException {
		return isToneExtension(file) && onPath(file);
	}

	static boolean onPath(IFile file) throws CoreException {
		return file.getProjectRelativePath().toString()
				.startsWith(PanettoneProject.TONE_INPUT);
	}

	static boolean isToneExtension(IFile file) {
		return file.getName().endsWith(".tone")
				|| file.getName().contains(".tone.");
	}

}
