package br.com.caelum.panettone.eclipse.builder;

import java.io.File;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.PanettoneProject;
import br.com.caelum.panettone.eclipse.ToneMarkers;

public class Builder {

	private final IProject project;
	private final PanettoneProject tone;

	public Builder(IProject project) {
		this.project = project;
		this.tone = new PanettoneProject(project);
	}
	
	void full() throws CoreException {
		clear();
		project.accept(new VisitToners(this::compile));
	}

	void incremental(IResourceDelta delta) throws CoreException {
		delta.accept(new DeltaVisitor(this::remove, this::compile));
	}

	private void clear() throws CoreException {
		new ToneMarkers().clear(project);
	}

	private void remove(IFile file) throws CoreException {
		tone.invokeOnCompiler("removeJavaVersionOf", new Class[]{String.class}, file.getFullPath().toPortableString());
	}

	@SuppressWarnings("unchecked")
	private void compile(IFile file) {
		new ToneMarkers().removeMarkersFor(file);
		PanettoneProject tone = new PanettoneProject(project);
		if(!tone.isEnabled()) return;
		try {
			Optional<Exception> ex = (Optional<Exception>) tone.invokeOnCompiler("compile", new Class[]{File.class}, file.getLocation().toFile()); 
			ex.ifPresent(e -> new ToneMarkers().addCompilationMarker(file, e));
		} catch (Exception e1) {
			new ToneMarkers().addCompilationMarker(file, e1);
		}
	}
}
