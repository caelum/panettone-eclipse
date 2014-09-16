package br.com.caelum.panettone.eclipse.builder.panettone;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.builder.CoreConsumer;
import br.com.caelum.panettone.eclipse.builder.FileVisitor;

class ToneVisitor extends FileVisitor {
	private final CoreConsumer<IFile> remove;
	private final CoreConsumer<IFile> compile;

	ToneVisitor(CoreConsumer<IFile> remove, CoreConsumer<IFile> compile) {
		this.remove = remove;
		this.compile = compile;
	}

	public boolean visit(IFile file, int deltaKind) throws CoreException {
		if (Tone.isTone(file))
			dealWith(deltaKind, file);
		return true;
	}

	private void dealWith(int deltaKind, IFile file) throws CoreException {
		switch (deltaKind) {
		case IResourceDelta.REMOVED:
			remove.accept(file);
			break;
		case IResourceDelta.ADDED:
		case IResourceDelta.CHANGED:
			compile.accept(file);
			break;
		}
	}
}
