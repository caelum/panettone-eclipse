package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

class CottiVisitor implements IResourceDeltaVisitor {
	private final CoreConsumer<IFile> compile;
	private boolean recompiled = false;
	CottiVisitor(CoreConsumer<IFile> compile) {
		this.compile = compile;
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		if(recompiled) return true;
		boolean notAFile = !(delta.getResource() instanceof IFile);
		if (notAFile)
			return true;
		
		IFile file = (IFile) delta.getResource();
		if (Tone.isCotti(file))
			dealWith(delta, file);
		return true;
	}

	private void dealWith(IResourceDelta delta, IFile file) throws CoreException {
		switch (delta.getKind()) {
		case IResourceDelta.REMOVED:
		case IResourceDelta.ADDED:
		case IResourceDelta.CHANGED:
			recompile(file);
			break;
		}
	}

	private void recompile(IFile file) throws CoreException {
		recompiled = true;
		compile.accept(file);
	}
}
