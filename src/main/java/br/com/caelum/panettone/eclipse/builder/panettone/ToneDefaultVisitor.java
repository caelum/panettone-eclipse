package br.com.caelum.panettone.eclipse.builder.panettone;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.builder.FileVisitor;

/**
 * Checks for tone.defaults modifications.
 * @author guilherme silveira
 * @author rodrigo turini
 * @author fernanda bernardo
 */
public class ToneDefaultVisitor extends FileVisitor {

	private boolean changed;

	public boolean visit(IFile file, int deltaKind) throws CoreException {
		if(file.getName().equals("tone.defaults"))
			changed = true;
		return false;
	}

	public boolean hasChanged() {
		return changed;
	}

}
