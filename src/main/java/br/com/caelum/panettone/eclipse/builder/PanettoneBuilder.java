package br.com.caelum.panettone.eclipse.builder;

import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import br.com.caelum.panettone.eclipse.PanettoneProject;

public class PanettoneBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "panettone-eclipse.panettoneBuilder";

	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		Builder builder = new Builder(getProject());

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
		getProject().getFolder(new PanettoneProject(getProject()).getViewOutput())
				.refreshLocal(DEPTH_INFINITE, monitor);
		return null;
	}

}
