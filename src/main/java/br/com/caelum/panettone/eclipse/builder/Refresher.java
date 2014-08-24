package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import br.com.caelum.vraptor.panettone.VRaptorCompiler;

public class Refresher {

	private IProject project;

	public Refresher(IProject project) {
		this.project = project;
	}

	public void all(IProgressMonitor monitor) throws CoreException {
		project.getFolder(VRaptorCompiler.VIEW_OUTPUT).refreshLocal(
				IResource.DEPTH_INFINITE, monitor);
	}

}
