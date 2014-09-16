package br.com.caelum.panettone.eclipse.builder.panettone;

import java.io.File;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.ToneMarkers;
import br.com.caelum.panettone.eclipse.builder.Build;

public class PanettoneBuild extends Build {

	public PanettoneBuild(IProject project) {
		super(project);
	}
	
	private void remove(IFile file) throws CoreException {
		vraptor.invokeOnTone("removeJavaVersionOf", new Class[]{String.class}, file.getFullPath().toPortableString());
	}

	@SuppressWarnings("unchecked")
	private void compileTone(IFile file) throws CoreException {
		new ToneMarkers().removeMarkersFor(file);
		Optional<Exception> ex = (Optional<Exception>) vraptor.invokeOnTone("compile", new Class[]{File.class}, file.getLocation().toFile()); 
		ex.ifPresent(e -> new ToneMarkers().addCompilationMarker(file, e));
	}

	public void incremental(IResourceDelta delta) throws CoreException {
		ToneDefaultVisitor defaults = new ToneDefaultVisitor();
		delta.accept(defaults);
		if(defaults.hasChanged()) {
			full();
		} else {
			delta.accept(new ToneVisitor(this::remove, this::compileTone));
		}
	}

	@Override
	public void full() throws CoreException {
		clear();
		project.accept(new AllToneVisitors(this::compileTone));
	}
}
