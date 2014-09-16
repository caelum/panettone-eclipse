package br.com.caelum.panettone.eclipse.builder.panettone;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.builder.CoreConsumer;

class AllToneVisitors implements IResourceVisitor {
	private final CoreConsumer<IFile> consumer;
	private static final boolean KEEP_VISITING = true;

	AllToneVisitors(CoreConsumer<IFile> consumer) {
		this.consumer = consumer;
	}

	public boolean visit(IResource resource) throws CoreException {
		boolean notAFile = !(resource instanceof IFile);
		if (notAFile)
			return true;
		IFile file = (IFile) resource;
		if (Tone.isTone(file))
			consumer.accept(file);
		return KEEP_VISITING;
	}

}
