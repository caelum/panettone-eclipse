package br.com.caelum.panettone.eclipse.loader;

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

class JarFileLocator implements IResourceVisitor {
	private Optional<IFile> jar = Optional.empty();
	
	private final String plugin;

	public JarFileLocator(String name) {
		this.plugin = name;
	}

	public boolean visit(IResource resource) throws CoreException {
		if (resource.getName().startsWith(plugin)) {
			jar = Optional.of((IFile) resource);
		}
		return true;
	}
	
	public Optional<IFile> getJar() {
		return jar;
	}
}