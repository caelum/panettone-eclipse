package br.com.caelum.panettone.eclipse;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import br.com.caelum.vraptor.panettone.VRaptorCompiler;

public class Builder {

	private final VRaptorCompiler compiler;
	private final IProject project;
	private final IProgressMonitor monitor;

	public Builder(IProject project, IProgressMonitor monitor,
			VRaptorCompiler compiler) {
		this.project = project;
		this.monitor = monitor;
		this.compiler = compiler;
	}

	void full() throws CoreException {
		clear();
		project.accept(new VisitToners(this::compile));
	}

	void incremental(IResourceDelta delta) throws CoreException {
		delta.accept(new DeltaVisitor(this::remove, this::compile));
	}

	private void clear() throws CoreException {
		project.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	private void remove(IFile file) {
		compiler.removeJavaVersionOf(file.getFullPath().toPortableString());
	}

	private void compile(IFile file) {
		deleteMarkers(file);
		try {
			List<Exception> errors = compiler.compileAndRetrieveErrors();
			for (Exception ex : errors) {
				addMarker(file, ex.getMessage(), 1, IMarker.SEVERITY_ERROR);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	private static final String MARKER_TYPE = "panettone-eclipse.toneProblem";

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

}
