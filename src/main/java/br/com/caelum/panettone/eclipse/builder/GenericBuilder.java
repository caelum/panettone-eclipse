package br.com.caelum.panettone.eclipse.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import br.com.caelum.panettone.eclipse.VRaptorProject;

public abstract class GenericBuilder extends IncrementalProjectBuilder {

	protected abstract Build getBuild();

	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		Build builder = getBuild();
	
		if (kind == FULL_BUILD) {
			builder.full();
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				builder.full();
			} else {
				builder.incremental(delta);
			}
		}
		new VRaptorProject(getProject()).refresh(monitor);
		return null;
	}

}