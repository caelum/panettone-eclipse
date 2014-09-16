package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * A visitor that visits only files
 * @author guilherme silveira
 * @author rodrigo turini
 * @author fernanda bernardo
 */
public abstract class FileVisitor implements IResourceDeltaVisitor {

	public final boolean visit(IResourceDelta delta) throws CoreException {
		boolean notAFile = !(delta.getResource() instanceof IFile);
		if (notAFile)
			return true;
		IFile file = (IFile) delta.getResource();
		return visit(file, delta.getKind());
	}

	protected abstract boolean visit(IFile file, int deltaKind) throws CoreException;
	
}
