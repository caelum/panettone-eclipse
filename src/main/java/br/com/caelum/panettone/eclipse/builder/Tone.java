package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.PanettoneProject;

public class Tone {

	static boolean isTone(IFile file) throws CoreException {
		return isToneExtension(file) && onTonePath(file);
	}

	static boolean onTonePath(IFile file) throws CoreException {
		return file.getProjectRelativePath().toString()
				.startsWith(PanettoneProject.TONE_INPUT);
	}

	static boolean onCottiPath(IFile file) throws CoreException {
		return file.getProjectRelativePath().toString()
				.startsWith(PanettoneProject.COTTI_INPUT);
	}

	static boolean isToneExtension(IFile file) {
		return file.getName().endsWith(".tone")
				|| file.getName().contains(".tone.");
	}

	static boolean isCottiExtension(IFile file) {
		return file.getName().endsWith(".properties")
				|| file.getName().startsWith("messages.");
	}

	static boolean isCotti(IFile file) throws CoreException {
		return isCottiExtension(file) && onCottiPath(file);
	}

}
