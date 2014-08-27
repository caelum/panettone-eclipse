package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

class VisitToners implements IResourceVisitor {
	private final CoreConsumer<IFile> consumer;
	private static final boolean KEEP_VISITING = true;

	VisitToners(CoreConsumer<IFile> consumer) {
		this.consumer = consumer;
	}

	public boolean visit(IResource resource) throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			if (Tone.isTone(file))
				consumer.accept(file);
		}
		return KEEP_VISITING;
	}

}
