package br.com.caelum.panettone.eclipse.builder;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import br.com.caelum.vraptor.panettone.VRaptorCompiler;

public class PanettoneBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "panettone-eclipse.panettoneBuilder";

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		Builder builder = getBuilder(monitor);

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
		new Refresher(getProject()).all(monitor);
		return null;
	}

	private Builder getBuilder(IProgressMonitor monitor) {
		VRaptorCompiler compiler = getCompiler();
		Builder builder = new Builder(getProject(), monitor, compiler);
		return builder;
	}

	private VRaptorCompiler getCompiler() {
		URI projectPath = getProject().getLocationURI();
		File baseDir = new File(projectPath);
		VRaptorCompiler compiler = new VRaptorCompiler(baseDir,
				Arrays.asList("java.util.*"));
		return compiler;
	}

}
