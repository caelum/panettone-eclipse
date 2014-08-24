package br.com.caelum.panettone.eclipse.builder;

import java.util.function.Consumer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

interface CoreConsumer<T> {
	void accept(T f) throws CoreException;
}
class DeltaVisitor implements IResourceDeltaVisitor {
	private final CoreConsumer<IFile> remove;
	private final Consumer<IFile> compile;

	DeltaVisitor(CoreConsumer<IFile> remove, Consumer<IFile> compile) {
		this.remove = remove;
		this.compile = compile;
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		boolean notAFile = !(delta.getResource() instanceof IFile);
		if (notAFile)
			return true;
		
		IFile file = (IFile) delta.getResource();
		if (Tone.isTone(file))
			dealWith(delta, file);
		return true;
	}

	private void dealWith(IResourceDelta delta, IFile file) throws CoreException {
		switch (delta.getKind()) {
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
