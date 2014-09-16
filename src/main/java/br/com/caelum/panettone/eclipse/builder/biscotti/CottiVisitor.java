package br.com.caelum.panettone.eclipse.builder.biscotti;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.builder.CoreConsumer;
import br.com.caelum.panettone.eclipse.builder.FileVisitor;
import br.com.caelum.panettone.eclipse.builder.panettone.Tone;

public class CottiVisitor extends FileVisitor {
	private final CoreConsumer<IFile> compile;
	public CottiVisitor(CoreConsumer<IFile> compile) {
		this.compile = compile;
	}

	public boolean visit(IFile file, int deltaKind) throws CoreException {
		if (Tone.isCotti(file))
			return dealWith(deltaKind, file);
		return true;
	}

	private boolean dealWith(int deltaKind, IFile file) throws CoreException {
		switch (deltaKind) {
		case IResourceDelta.REMOVED:
		case IResourceDelta.ADDED:
		case IResourceDelta.CHANGED:
			recompile(file);
			return false;
		}
		return true;
	}

	private void recompile(IFile file) throws CoreException {
		compile.accept(file);
	}
}
