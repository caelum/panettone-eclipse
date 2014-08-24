package br.com.caelum.panettone.eclipse;

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

class JarFileLocator implements IResourceVisitor {
	private Optional<IFile> jar = Optional.empty();

	public boolean visit(IResource resource) throws CoreException {
		if (resource.getName().contains("vraptor-panettone")) {
			jar = Optional.of((IFile) resource);
		}
		return true;
	}
	
	public Optional<IFile> getJar() {
		return jar;
	}
}