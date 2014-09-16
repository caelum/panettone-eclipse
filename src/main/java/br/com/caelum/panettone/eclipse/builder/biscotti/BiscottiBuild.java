package br.com.caelum.panettone.eclipse.builder.biscotti;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.VRaptorProject;
import br.com.caelum.panettone.eclipse.builder.Build;

public class BiscottiBuild extends Build {

	public BiscottiBuild(IProject project) {
		super(project);
	}

	public void full() throws CoreException {
		clear();
		compileCotti(null);
	}

	public void incremental(IResourceDelta delta) throws CoreException {
		delta.accept(new CottiVisitor(this::compileCotti));
	}

	private void compileCotti(IFile file) throws CoreException {
		File folder = project.getFolder(VRaptorProject.COTTI_INPUT).getLocation().toFile();
		vraptor.invokeOnCotti("compile", new Class[]{File.class}, folder); 
	}
}
