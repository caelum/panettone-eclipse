package br.com.caelum.panettone.eclipse;

import java.util.function.Consumer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

class VisitToners implements IResourceVisitor {
	private final Consumer<IFile> consumer;
	private static final boolean KEEP_VISITING = true;

	VisitToners(Consumer<IFile> consumer) {
		this.consumer = consumer;
	}

	public boolean visit(IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			if (Tone.isTone(file))
				consumer.accept(file);
		}
		return KEEP_VISITING;
	}

}
