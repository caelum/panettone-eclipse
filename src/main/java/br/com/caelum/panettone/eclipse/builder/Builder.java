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
		project.accept(new VisitToners(this::compileTone));
		compileCotti(null);
	}

	void incremental(IResourceDelta delta) throws CoreException {
		delta.accept(new DeltaVisitor(this::remove, this::compileTone));
		delta.accept(new CottiVisitor(this::compileCotti));
	}

	private void clear() throws CoreException {
		new ToneMarkers().clear(project);
	}

	private void remove(IFile file) throws CoreException {
		tone.invokeOnTone("removeJavaVersionOf", new Class[]{String.class}, file.getFullPath().toPortableString());
	}

	@SuppressWarnings("unchecked")
	private void compileTone(IFile file) throws CoreException {
		new ToneMarkers().removeMarkersFor(file);
		Optional<Exception> ex = (Optional<Exception>) tone.invokeOnTone("compile", new Class[]{File.class}, file.getLocation().toFile()); 
		ex.ifPresent(e -> new ToneMarkers().addCompilationMarker(file, e));
	}

	private void compileCotti(IFile file) throws CoreException {
		File folder = project.getFolder(PanettoneProject.COTTI_INPUT).getLocation().toFile();
		tone.invokeOnCotti("compile", new Class[]{File.class}, folder); 
	}
}
