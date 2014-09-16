package br.com.caelum.panettone.eclipse.builder.panettone;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.PanettoneProject;
import br.com.caelum.panettone.eclipse.builder.Filenames;

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
		return Filenames.isTone(file.getName());
	}

	static boolean isCottiExtension(IFile file) {
		return Filenames.isCotti(file.getName());
	}

	public static boolean isCotti(IFile file) throws CoreException {
		return isCottiExtension(file) && onCottiPath(file);
	}

}
