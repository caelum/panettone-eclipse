package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.ToneMarkers;
import br.com.caelum.panettone.eclipse.VRaptorProject;

public abstract class Build {

	protected final IProject project;
	protected final VRaptorProject vraptor;

	public Build(IProject project) {
		this.project = project;
		this.vraptor = new VRaptorProject(project);
	}

	public abstract void full() throws CoreException;
	
	protected void clear() throws CoreException {
		new ToneMarkers().clear(project);
	}

	protected abstract void incremental(IResourceDelta delta) throws CoreException;

}
